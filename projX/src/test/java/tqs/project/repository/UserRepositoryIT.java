package tqs.project.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import tqs.project.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("Deve encontrar utilizador por email")
    void shouldFindUserByEmail() {
        // Given
        User user = new User();
        user.setName("João");
        user.setEmail("joao@test.com");
        user.setPassword("password123");
        user.setBalance(100.0);
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("joao@test.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("João");
        assertThat(found.get().getBalance()).isEqualTo(100.0);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("Deve retornar empty quando email não existe")
    void shouldReturnEmptyWhenEmailNotExists() {
        Optional<User> found = userRepository.findByEmail("naoexiste@test.com");
        assertThat(found).isEmpty();
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("Deve salvar e encontrar utilizador por ID")
    void shouldSaveAndFindById() {
        User user = new User();
        user.setName("Maria");
        user.setEmail("maria@test.com");
        user.setPassword("password456");
        user.setBalance(200.0);

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Maria");
    }
}