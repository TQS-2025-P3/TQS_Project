package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.CarDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookChargeRepository bookChargeRepository;

    @InjectMocks
    private CarService carService;

    private Car car;
    private User user;
    private CarDTO carDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João");

        car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setModel("Model 3");
        car.setPlate("AB-12-CD");
        car.setBatteryCapacity(75.0);
        car.setOwner(user);

        carDTO = new CarDTO();
        carDTO.setBrand("Tesla");
        carDTO.setModel("Model 3");
        carDTO.setPlate("AB-12-CD");
        carDTO.setBatteryCapacity(75.0);
        carDTO.setUserId(1L);
    }

    @Test
    @DisplayName("Deve retornar todos os carros")
    void shouldReturnAllCars() {
        List<Car> cars = Arrays.asList(car);
        when(carRepository.findAll()).thenReturn(cars);

        List<Car> result = carService.getAllCars();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Tesla");
        verify(carRepository).findAll();
    }

    @Test
    @DisplayName("Deve adicionar carro com sucesso")
    void shouldAddCarSuccessfully() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList()); 
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.addCar(carDTO);

        assertThat(result).isNotNull();
        assertThat(result.getBrand()).isEqualTo("Tesla");
        verify(userRepository).findById(1L);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar carro com matrícula duplicada")
    void shouldThrowExceptionWhenAddingCarWithDuplicatePlate() {
        Car existingCar = new Car();
        existingCar.setId(2L);
        existingCar.setPlate("AB-12-CD");

        when(carRepository.findAll()).thenReturn(Arrays.asList(existingCar));

        assertThatThrownBy(() -> carService.addCar(carDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe um carro com a matrícula: AB-12-CD");

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar carro com matrícula duplicada em formato diferente")
    void shouldThrowExceptionWhenAddingCarWithDuplicatePlateInDifferentFormat() {
        Car existingCar = new Car();
        existingCar.setId(2L);
        existingCar.setPlate("AB12CD"); 
        carDTO.setPlate("AB-12-CD");

        when(carRepository.findAll()).thenReturn(Arrays.asList(existingCar));

        assertThatThrownBy(() -> carService.addCar(carDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe um carro com a matrícula: AB-12-CD");

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar carro a utilizador inexistente")
    void shouldThrowExceptionWhenAddingCarToNonExistentUser() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList()); 
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        carDTO.setUserId(999L);

        assertThatThrownBy(() -> carService.addCar(carDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Utilizador não encontrado");

        verify(userRepository).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve normalizar matrícula com sucesso - teste com matrícula null")
    void shouldHandleNullPlateInNormalization() {
        carDTO.setPlate(null);

        when(carRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.addCar(carDTO);

        assertThat(result).isNotNull();
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve retornar carro por ID")
    void shouldReturnCarById() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Car result = carService.getCarById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getBrand()).isEqualTo("Tesla");
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar null quando carro não existe")
    void shouldReturnNullWhenCarNotExists() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        Car result = carService.getCarById(999L);

        assertThat(result).isNull();
        verify(carRepository).findById(999L);
    }

    @Test
    @DisplayName("Deve retornar carros por ID do utilizador")
    void shouldReturnCarsByUserId() {
        List<Car> cars = Arrays.asList(car);
        when(carRepository.findByOwnerId(1L)).thenReturn(cars);

        List<Car> result = carService.getCarsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Tesla");
        verify(carRepository).findByOwnerId(1L);
    }

    @Test
    @DisplayName("Deve eliminar carro por ID")
    void shouldDeleteCarById() {
        when(bookChargeRepository.findByCarIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());

        carService.deleteCarById(1L);

        verify(bookChargeRepository).findByCarIdAndStatus(1L, BookingStatus.RESERVED);
        verify(bookChargeRepository).deleteByCarId(1L);
        verify(carRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao eliminar carro com reservas ativas")
    void shouldThrowExceptionWhenDeletingCarWithActiveBookings() {
        BookCharge activeBooking = new BookCharge();
        activeBooking.setId(100L);

        when(bookChargeRepository.findByCarIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Arrays.asList(activeBooking));

        assertThatThrownBy(() -> carService.deleteCarById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Não é possível apagar o carro. Existem 1 reserva(s) ativa(s)");

        verify(bookChargeRepository).findByCarIdAndStatus(1L, BookingStatus.RESERVED);
        verify(carRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve atualizar carro existente")
    void shouldUpdateExistingCar() {
        CarDTO updateDTO = new CarDTO();
        updateDTO.setBrand("BMW");
        updateDTO.setModel("i3");
        updateDTO.setPlate("XY-34-ZW");
        updateDTO.setBatteryCapacity(42.2);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.findAll()).thenReturn(Arrays.asList(car)); 
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(carRepository).findById(1L);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar carro com a mesma matrícula")
    void shouldAllowUpdateCarWithSamePlate() {
        CarDTO updateDTO = new CarDTO();
        updateDTO.setBrand("Tesla");
        updateDTO.setModel("Model S");
        updateDTO.setPlate("AB-12-CD");
        updateDTO.setBatteryCapacity(100.0);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.findAll()).thenReturn(Arrays.asList(car)); 
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(carRepository).findById(1L);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar carro com matrícula duplicada de outro carro")
    void shouldThrowExceptionWhenUpdatingCarWithDuplicatePlateFromOtherCar() {
        CarDTO updateDTO = new CarDTO();
        updateDTO.setBrand("BMW");
        updateDTO.setModel("i3");
        updateDTO.setPlate("XY-34-ZW");
        updateDTO.setBatteryCapacity(42.2);

        Car otherCarWithSamePlate = new Car();
        otherCarWithSamePlate.setId(2L);
        otherCarWithSamePlate.setPlate("XY-34-ZW");

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.findAll()).thenReturn(Arrays.asList(car, otherCarWithSamePlate));

        assertThatThrownBy(() -> carService.updateCar(1L, updateDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe outro carro com a matrícula: XY-34-ZW");

        verify(carRepository).findById(1L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar carro com matrícula duplicada em formato diferente")
    void shouldThrowExceptionWhenUpdatingCarWithDuplicatePlateInDifferentFormat() {
        CarDTO updateDTO = new CarDTO();
        updateDTO.setBrand("BMW");
        updateDTO.setModel("i3");
        updateDTO.setPlate("XY-34-ZW"); 
        updateDTO.setBatteryCapacity(42.2);

        Car otherCarWithSamePlate = new Car();
        otherCarWithSamePlate.setId(2L); 
        otherCarWithSamePlate.setPlate("XY34ZW"); 

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.findAll()).thenReturn(Arrays.asList(car, otherCarWithSamePlate));

        assertThatThrownBy(() -> carService.updateCar(1L, updateDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe outro carro com a matrícula: XY-34-ZW");

        verify(carRepository).findById(1L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar carro inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentCar() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.updateCar(999L, carDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Carro não encontrado");

        verify(carRepository).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve normalizar corretamente matrículas com espaços e caracteres especiais")
    void shouldNormalizePlatesWithSpacesAndSpecialCharacters() {
        CarDTO carDTOWithSpaces = new CarDTO();
        carDTOWithSpaces.setBrand("BMW");
        carDTOWithSpaces.setModel("i3");
        carDTOWithSpaces.setPlate("AB 12 CD");
        carDTOWithSpaces.setBatteryCapacity(42.2);
        carDTOWithSpaces.setUserId(1L);

        Car existingCarWithHyphens = new Car();
        existingCarWithHyphens.setId(2L);
        existingCarWithHyphens.setPlate("AB-12-CD"); 

        when(carRepository.findAll()).thenReturn(Arrays.asList(existingCarWithHyphens));

        assertThatThrownBy(() -> carService.addCar(carDTOWithSpaces))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe um carro com a matrícula: AB 12 CD");

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve tratar matrícula null durante verificação de duplicação")
    void shouldHandleNullPlateInDuplicationCheck() {
        CarDTO carDTOWithDifferentPlate = new CarDTO();
        carDTOWithDifferentPlate.setBrand("BMW");
        carDTOWithDifferentPlate.setModel("i3");
        carDTOWithDifferentPlate.setPlate("XY-99-ZZ");
        carDTOWithDifferentPlate.setBatteryCapacity(42.2);
        carDTOWithDifferentPlate.setUserId(1L);

        Car existingCarWithNullPlate = new Car();
        existingCarWithNullPlate.setId(2L);
        existingCarWithNullPlate.setPlate(null);

        when(carRepository.findAll()).thenReturn(Arrays.asList(existingCarWithNullPlate));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.addCar(carDTOWithDifferentPlate);

        assertThat(result).isNotNull();
        verify(carRepository).save(any(Car.class));
    }
}