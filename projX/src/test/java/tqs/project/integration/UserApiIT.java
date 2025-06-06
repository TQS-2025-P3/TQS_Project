package tqs.project.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import tqs.project.dto.UserDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class UserApiIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("Deve criar utilizador via API")
    void shouldCreateUserViaApi() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("João API");
        userDTO.setEmail("joao.api@test.com");
        userDTO.setPassword("password123");

        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/api/users")
        .then()
            .statusCode(200)
            .body("name", equalTo("João API"))
            .body("email", equalTo("joao.api@test.com"))
            .body("id", notNullValue());
    }

    @Test
    @DisplayName("Deve listar utilizadores via API")
    void shouldListUsersViaApi() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Maria API");
        userDTO.setEmail("maria.api@test.com");
        userDTO.setPassword("password456");

        given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/api/users");

        given()
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].name", notNullValue());
    }

    @Test
    @DisplayName("Deve adicionar fundos via API")
    void shouldAddFundsViaApi() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Pedro API");
        userDTO.setEmail("pedro.api@test.com");
        userDTO.setPassword("password789");

        Integer userId = given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/api/users")
        .then()
            .statusCode(200)
            .extract()
            .path("id");

        given()
            .param("amount", 100.0)
        .when()
            .patch("/api/users/{id}/addFunds", userId)
        .then()
            .statusCode(200)
            .body("balance", equalTo(100.0f));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao adicionar fundos negativos")
    void shouldReturn400WhenAddingNegativeFunds() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Ana API");
        userDTO.setEmail("ana.api@test.com");
        userDTO.setPassword("password000");

        Integer userId = given()
            .contentType(ContentType.JSON)
            .body(userDTO)
        .when()
            .post("/api/users")
        .then()
            .extract()
            .path("id");

        given()
            .param("amount", -50.0)
        .when()
            .patch("/api/users/{id}/addFunds", userId)
        .then()
            .statusCode(400)
            .body(containsString("Erro ao adicionar fundos"));
    }
}