package tqs.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car();
    }

    @Test
    @DisplayName("Deve criar carro com construtor vazio")
    void shouldCreateCarWithNoArgsConstructor() {
        assertThat(car).isNotNull();
        assertThat(car.getBatteryCapacity()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Deve criar carro com todos os argumentos")
    void shouldCreateCarWithAllArgsConstructor() {
        User owner = new User();
        owner.setName("Pedro");
        
        Car fullCar = new Car(1L, "Tesla", "Model 3", "AB-12-CD", 75.0, owner);

        assertThat(fullCar.getId()).isEqualTo(1L);
        assertThat(fullCar.getBrand()).isEqualTo("Tesla");
        assertThat(fullCar.getModel()).isEqualTo("Model 3");
        assertThat(fullCar.getPlate()).isEqualTo("AB-12-CD");
        assertThat(fullCar.getBatteryCapacity()).isEqualTo(75.0);
        assertThat(fullCar.getOwner().getName()).isEqualTo("Pedro");
    }

    @Test
    @DisplayName("Deve configurar propriedades do carro")
    void shouldSetCarProperties() {
        User owner = new User();
        owner.setName("Maria");
        
        car.setId(2L);
        car.setBrand("BMW");
        car.setModel("i3");
        car.setPlate("XY-34-ZW");
        car.setBatteryCapacity(42.2);
        car.setOwner(owner);

        assertThat(car.getId()).isEqualTo(2L);
        assertThat(car.getBrand()).isEqualTo("BMW");
        assertThat(car.getModel()).isEqualTo("i3");
        assertThat(car.getPlate()).isEqualTo("XY-34-ZW");
        assertThat(car.getBatteryCapacity()).isEqualTo(42.2);
        assertThat(car.getOwner().getName()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("Deve permitir capacidade de bateria zero")
    void shouldAllowZeroBatteryCapacity() {
        car.setBatteryCapacity(0.0);
        assertThat(car.getBatteryCapacity()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Deve aceitar diferentes formatos de matrícula")
    void shouldAcceptDifferentPlateFormats() {
        car.setPlate("00-AA-00");
        assertThat(car.getPlate()).isEqualTo("00-AA-00");
        
        car.setPlate("AB-12-34");
        assertThat(car.getPlate()).isEqualTo("AB-12-34");
    }
}
