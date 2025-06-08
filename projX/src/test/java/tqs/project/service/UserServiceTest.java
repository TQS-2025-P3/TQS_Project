package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.CarDTO;
import tqs.project.dto.UserDTO;
import tqs.project.model.BookCharge;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.BookChargeRepository;
import tqs.project.repository.CarRepository;
import tqs.project.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookChargeRepository bookChargeRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@test.com");
        user.setPassword("password123");
        user.setBalance(100.0);

        userDTO = new UserDTO();
        userDTO.setName("João");
        userDTO.setEmail("joao@test.com");
        userDTO.setPassword("password123");
    }

    @Test
    @DisplayName("Deve retornar todos os utilizadores")
    void shouldReturnAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("João");
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar utilizador por ID")
    void shouldReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar null quando utilizador não existe")
    void shouldReturnNullWhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.getUserById(999L);

        assertThat(result).isNull();
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve criar utilizador sem carros")
    void shouldCreateUserWithoutCars() {
        when(userRepository.findByEmailIgnoreCase("joao@test.com")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve criar utilizador com carros")
    void shouldCreateUserWithCars() {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Tesla");
        carDTO.setModel("Model 3");
        carDTO.setPlate("AB-12-CD");
        carDTO.setBatteryCapacity(75.0);
        userDTO.setCars(Arrays.asList(carDTO));

        when(userRepository.findByEmailIgnoreCase("joao@test.com")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar utilizador com email duplicado")
    void shouldThrowExceptionWhenCreatingUserWithDuplicateEmail() {
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("joao@test.com");
        
        when(userRepository.findByEmailIgnoreCase("joao@test.com")).thenReturn(new java.util.ArrayList<>(Arrays.asList(existingUser)));

        assertThatThrownBy(() -> userService.createUser(userDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe um utilizador com o email: joao@test.com");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar utilizador existente")
    void shouldUpdateExistingUser() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("joao.silva@test.com");
        updateDTO.setBalance(200.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("joao.silva@test.com")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar utilizador com o mesmo email")
    void shouldAllowUpdateUserWithSameEmail() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("joao@test.com"); 
        updateDTO.setPassword("newPassword");

        User existingUserWithSameEmail = new User();
        existingUserWithSameEmail.setId(1L); 
        existingUserWithSameEmail.setEmail("joao@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("joao@test.com")).thenReturn(new java.util.ArrayList<>(Arrays.asList(existingUserWithSameEmail)));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar utilizador com email duplicado de outro utilizador")
    void shouldThrowExceptionWhenUpdatingUserWithDuplicateEmailFromOtherUser() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("outro@test.com");

        User otherUserWithSameEmail = new User();
        otherUserWithSameEmail.setId(2L); 
        otherUserWithSameEmail.setEmail("outro@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("outro@test.com")).thenReturn(new java.util.ArrayList<>(Arrays.asList(otherUserWithSameEmail)));

        assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe outro utilizador com o email: outro@test.com");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar utilizador com carros")
    void shouldUpdateUserWithCars() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("joao.silva@test.com");
        
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("BMW");
        carDTO.setModel("i3");
        carDTO.setPlate("XY-34-ZW");
        carDTO.setBatteryCapacity(42.2);
        updateDTO.setCars(Arrays.asList(carDTO));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("joao.silva@test.com")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar utilizador sem alterar password quando password é null")
    void shouldUpdateUserWithoutChangingPasswordWhenPasswordIsNull() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("joao.silva@test.com");
        updateDTO.setPassword(null); 

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("joao.silva@test.com")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar utilizador sem alterar password quando password é vazia")
    void shouldUpdateUserWithoutChangingPasswordWhenPasswordIsEmpty() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("joao.silva@test.com");
        updateDTO.setPassword(""); 

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("joao.silva@test.com")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar utilizador inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, userDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Utilizador com ID 999 não encontrado");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve adicionar fundos ao utilizador")
    void shouldAddFundsToUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addFunds(1L, 50.0);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar montante negativo")
    void shouldThrowExceptionWhenAddingNegativeAmount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addFunds(1L, -10.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Montante inválido");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar fundos a utilizador inexistente")
    void shouldThrowExceptionWhenAddingFundsToNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFunds(999L, 50.0))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Utilizador não encontrado");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar saldo do utilizador")
    void shouldReturnUserBalance() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Double balance = userService.getUserBalance(1L);

        assertThat(balance).isEqualTo(100.0);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter saldo de utilizador inexistente")
    void shouldThrowExceptionWhenGettingBalanceOfNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserBalance(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Utilizador não encontrado");

        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve eliminar utilizador existente")
    void shouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(carRepository.findByOwnerId(1L)).thenReturn(Collections.emptyList());
        when(bookChargeRepository.findByUserIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(carRepository).findByOwnerId(1L);
        verify(bookChargeRepository).findByUserIdAndStatus(1L, BookingStatus.RESERVED);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve eliminar utilizador com carros sem reservas ativas")
    void shouldDeleteUserWithCarsWithoutActiveBookings() {
        Car car = new Car();
        car.setId(10L);
        car.setOwner(user);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(carRepository.findByOwnerId(1L)).thenReturn(Arrays.asList(car));
        when(bookChargeRepository.findByCarIdAndStatus(10L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());
        when(bookChargeRepository.findByUserIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(carRepository).findByOwnerId(1L);
        verify(bookChargeRepository).findByCarIdAndStatus(10L, BookingStatus.RESERVED);
        verify(bookChargeRepository).findByUserIdAndStatus(1L, BookingStatus.RESERVED);
        verify(bookChargeRepository).deleteByCarId(10L);
        verify(bookChargeRepository).deleteByUserId(1L);
        verify(carRepository).deleteAll(Arrays.asList(car));
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao eliminar utilizador com reservas ativas nos carros")
    void shouldThrowExceptionWhenDeletingUserWithActiveBookingsInCars() {
        Car car = new Car();
        car.setId(10L);
        car.setOwner(user);

        BookCharge activeBooking = new BookCharge();
        activeBooking.setId(100L);
        activeBooking.setCar(car);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(carRepository.findByOwnerId(1L)).thenReturn(Arrays.asList(car));
        when(bookChargeRepository.findByCarIdAndStatus(10L, BookingStatus.RESERVED))
            .thenReturn(Arrays.asList(activeBooking));

        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Não é possível apagar o utilizador. Existem 1 reserva(s) ativa(s) associada(s)");

        verify(userRepository).existsById(1L);
        verify(carRepository).findByOwnerId(1L);
        verify(bookChargeRepository).findByCarIdAndStatus(10L, BookingStatus.RESERVED);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao eliminar utilizador com reservas diretas ativas")
    void shouldThrowExceptionWhenDeletingUserWithDirectActiveBookings() {
        BookCharge directActiveBooking = new BookCharge();
        directActiveBooking.setId(200L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(carRepository.findByOwnerId(1L)).thenReturn(Collections.emptyList());
        when(bookChargeRepository.findByUserIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Arrays.asList(directActiveBooking));

        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Não é possível apagar o utilizador. Existem 1 reserva(s) ativa(s) associada(s)");

        verify(userRepository).existsById(1L);
        verify(carRepository).findByOwnerId(1L);
        verify(bookChargeRepository).findByUserIdAndStatus(1L, BookingStatus.RESERVED);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao eliminar utilizador inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Utilizador com ID 999 não encontrado");

        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve retornar carros do utilizador")
    void shouldReturnUserCars() {
        Car car = new Car();
        car.setBrand("Tesla");
        user.setCars(Arrays.asList(car));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<Car> result = userService.getCarsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Tesla");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando utilizador não tem carros")
    void shouldReturnEmptyListWhenUserHasNoCars() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        List<Car> result = userService.getCarsByUserId(999L);

        assertThat(result).isEmpty();
        verify(userRepository).findById(999L);
    }
}