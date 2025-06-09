package tqs.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.dto.UserDTO;
import tqs.project.model.User;
import tqs.project.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("GET /api/users - Deve retornar todos os utilizadores")
    void shouldReturnAllUsers() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("João"))
                .andExpect(jsonPath("$[0].email").value("joao@test.com"));

        verify(userService).getAllUsers();
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("POST /api/users - Deve criar utilizador com sucesso")
    void shouldCreateUserSuccessfully() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.email").value("joao@test.com"));

        verify(userService).createUser(any(UserDTO.class));
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("POST /api/users - Deve retornar erro quando criar utilizador falha")
    void shouldReturnErrorWhenCreateUserFails() throws Exception {
        when(userService.createUser(any(UserDTO.class)))
            .thenThrow(new RuntimeException("Já existe um utilizador com o email: joao@test.com"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Já existe um utilizador com o email: joao@test.com"));

        verify(userService).createUser(any(UserDTO.class));
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("GET /api/users/{id} - Deve retornar utilizador por ID")
    void shouldReturnUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.email").value("joao@test.com"));

        verify(userService).getUserById(1L);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("GET /api/users/{id} - Deve retornar 404 quando utilizador não existe")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        when(userService.getUserById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("PATCH /api/users/{id}/addFunds - Deve adicionar fundos com sucesso")
    void shouldAddFundsSuccessfully() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setBalance(150.0);
        
        when(userService.addFunds(1L, 50.0)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1/addFunds")
                .param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.0));

        verify(userService).addFunds(1L, 50.0);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("PATCH /api/users/{id}/addFunds - Deve retornar erro quando falha com RuntimeException")
    void shouldReturnErrorWhenAddFundsFails() throws Exception {
        when(userService.addFunds(1L, -10.0))
            .thenThrow(new RuntimeException("Montante inválido"));

        mockMvc.perform(patch("/api/users/1/addFunds")
                .param("amount", "-10.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao adicionar fundos: Montante inválido"));

        verify(userService).addFunds(1L, -10.0);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("PATCH /api/users/{id}/addFunds - Deve retornar erro quando falha com IllegalArgumentException")
    void shouldReturnErrorWhenAddFundsFailsWithIllegalArgument() throws Exception {
        when(userService.addFunds(1L, -10.0))
            .thenThrow(new IllegalArgumentException("Montante inválido"));

        mockMvc.perform(patch("/api/users/1/addFunds")
                .param("amount", "-10.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao adicionar fundos: Montante inválido"));

        verify(userService).addFunds(1L, -10.0);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("GET /api/users/{id}/balance - Deve retornar saldo do utilizador")
    void shouldReturnUserBalance() throws Exception {
        when(userService.getUserBalance(1L)).thenReturn(100.0);

        mockMvc.perform(get("/api/users/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100.0));

        verify(userService).getUserBalance(1L);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("PUT /api/users/{id} - Deve atualizar utilizador")
    void shouldUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João"));

        verify(userService).updateUser(eq(1L), any(UserDTO.class));
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("PUT /api/users/{id} - Deve retornar 404 quando utilizador não existe para atualizar")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        when(userService.updateUser(eq(999L), any(UserDTO.class)))
            .thenThrow(new IllegalArgumentException("Utilizador com ID 999 não encontrado"));

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any(UserDTO.class));
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("PUT /api/users/{id} - Deve retornar erro quando atualizar utilizador falha")
    void shouldReturnErrorWhenUpdateUserFails() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDTO.class)))
            .thenThrow(new RuntimeException("Já existe outro utilizador com o email: joao@test.com"));

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Já existe outro utilizador com o email: joao@test.com"));

        verify(userService).updateUser(eq(1L), any(UserDTO.class));
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("DELETE /api/users/{id} - Deve eliminar utilizador com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("DELETE /api/users/{id} - Deve retornar 404 quando utilizador não existe")
    void shouldReturn404WhenUserNotExists() throws Exception {
        doThrow(new IllegalArgumentException("Utilizador não encontrado"))
            .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(999L);
    }

    @Requirement("TQSPROJECT-1221")
    @Test
    @DisplayName("DELETE /api/users/{id} - Deve retornar erro quando eliminar utilizador falha")
    void shouldReturnErrorWhenDeleteUserFails() throws Exception {
        doThrow(new RuntimeException("Não é possível apagar o utilizador. Existem reservas ativas"))
            .when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Não é possível apagar o utilizador. Existem reservas ativas"));

        verify(userService).deleteUser(1L);
    }
}