package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.CarDTO;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.repository.CarRepository;
import tqs.project.repository.UserRepository;

import java.util.Arrays;
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
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.addCar(carDTO);

        assertThat(result).isNotNull();
        assertThat(result.getBrand()).isEqualTo("Tesla");
        verify(userRepository).findById(1L);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar carro a utilizador inexistente")
    void shouldThrowExceptionWhenAddingCarToNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        carDTO.setUserId(999L);

        assertThatThrownBy(() -> carService.addCar(carDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Utilizador não encontrado");

        verify(userRepository).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
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
        carService.deleteCarById(1L);

        verify(carRepository).deleteById(1L);
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
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(carRepository).findById(1L);
        verify(carRepository).save(any(Car.class));
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
}