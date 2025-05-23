package tqs.project.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import tqs.project.dto.CarDTO;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.repository.CarRepository;
import tqs.project.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCarSuccess() {
        Long userId = 1L;
        CarDTO dto = new CarDTO("Tesla", "Model 3", 450, userId);
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        Car createdCar = carService.addCar(dto);

        assertNotNull(createdCar);
        assertEquals("Tesla", createdCar.getBrand());
        assertEquals("Model 3", createdCar.getModel());
        assertEquals(450, createdCar.getRangeKm());
        assertEquals(user, createdCar.getOwner());
    }

    @Test
    void testAddCarWithInvalidUser() {
        CarDTO dto = new CarDTO("Renault", "ZOE", 300, 999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

       
        Car createdCar = carService.addCar(dto);

        
        assertNull(createdCar);
    }


    @Test
void testGetCarByIdFound() {
    Car car = new Car();
    car.setId(1L);
    car.setBrand("BMW");
    car.setModel("i3");
    car.setRangeKm(200);

    when(carRepository.findById(1L)).thenReturn(Optional.of(car));

    // Act
    Car result = carService.getCarById(1L);

    // Assert
    assertNotNull(result);
    assertEquals("BMW", result.getBrand());
}

@Test
void testGetCarByIdNotFound() {
    when(carRepository.findById(99L)).thenReturn(Optional.empty());

    Car result = carService.getCarById(99L);

    assertNull(result);
}

@Test
void testGetAllCars() {
    Car car1 = new Car();
    car1.setBrand("BMW");
    car1.setModel("i3");

    Car car2 = new Car();
    car2.setBrand("Tesla");
    car2.setModel("Model X");

    when(carRepository.findAll()).thenReturn(List.of(car1, car2));

    List<Car> allCars = carService.getAllCars();

    assertEquals(2, allCars.size());
    assertEquals("BMW", allCars.get(0).getBrand());
    assertEquals("Tesla", allCars.get(1).getBrand());
}

}
