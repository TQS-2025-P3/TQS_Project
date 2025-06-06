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
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.repository.UserRepository;

import java.util.Arrays;
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

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar utilizador existente")
    void shouldUpdateExistingUser() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("João Silva");
        updateDTO.setEmail("joao.silva@test.com");
        updateDTO.setBalance(200.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
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

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
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