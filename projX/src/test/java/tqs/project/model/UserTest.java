package tqs.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Deve criar usuário com construtor vazio")
    void shouldCreateUserWithNoArgsConstructor() {
        assertThat(user).isNotNull();
        assertThat(user.getBalance()).isEqualTo(0.0);
        assertThat(user.getCars()).isNull();
    }

    @Test
    @DisplayName("Deve criar usuário com todos os argumentos")
    void shouldCreateUserWithAllArgsConstructor() {
        Car car = new Car();
        car.setBrand("Tesla");
        
        User fullUser = new User(1L, Arrays.asList(car), 100.0);
        fullUser.setName("João");
        fullUser.setEmail("joao@test.com");
        fullUser.setPassword("password123");

        assertThat(fullUser.getId()).isEqualTo(1L);
        assertThat(fullUser.getName()).isEqualTo("João");
        assertThat(fullUser.getEmail()).isEqualTo("joao@test.com");
        assertThat(fullUser.getPassword()).isEqualTo("password123");
        assertThat(fullUser.getBalance()).isEqualTo(100.0);
        assertThat(fullUser.getCars()).hasSize(1);
        assertThat(fullUser.getCars().get(0).getBrand()).isEqualTo("Tesla");
    }

    @Test
    @DisplayName("Deve configurar e obter propriedades corretamente")
    void shouldSetAndGetPropertiesCorrectly() {
        Car car1 = new Car();
        Car car2 = new Car();
        
        user.setId(2L);
        user.setName("Maria");
        user.setEmail("maria@test.com");
        user.setPassword("senha456");
        user.setBalance(250.75);
        user.setCars(Arrays.asList(car1, car2));

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getName()).isEqualTo("Maria");
        assertThat(user.getEmail()).isEqualTo("maria@test.com");
        assertThat(user.getPassword()).isEqualTo("senha456");
        assertThat(user.getBalance()).isEqualTo(250.75);
        assertThat(user.getCars()).hasSize(2);
    }

    @Test
    @DisplayName("Deve permitir balance negativo")
    void shouldAllowNegativeBalance() {
        user.setBalance(-50.0);
        assertThat(user.getBalance()).isEqualTo(-50.0);
    }

    @Test
    @DisplayName("Deve permitir lista de carros vazia")
    void shouldAllowEmptyCarsList() {
        user.setCars(new ArrayList<>());
        assertThat(user.getCars()).isEmpty();
    }
}