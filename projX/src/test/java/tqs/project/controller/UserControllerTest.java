package tqs.project.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import tqs.project.service.UserService;
import tqs.project.model.User;
import tqs.project.model.Car;

import java.util.Arrays;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUserSuccess() throws Exception {
        User user = new User();
        user.setName("Joana");
        user.setEmail("joana@example.com");
        user.setPassword("abc123");

        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joana"));
    }

    @Test
    void testCreateUserInvalidEmail() throws Exception {
        User user = new User();
        user.setName("NoEmail");
        user.setPassword("123456");

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserWithCars() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Carlos");
        user.setEmail("carlos@example.com");
        user.setPassword("pass");

        Car car1 = new Car();
        car1.setId(1L);
        car1.setBrand("Tesla");
        car1.setModel("Model S");
        car1.setPlate("11-AA-11");
        car1.setBatteryCapacity(100.0);
        car1.setOwner(user);

        Car car2 = new Car();
        car2.setId(2L);
        car2.setBrand("Nissan");
        car2.setModel("Leaf");
        car2.setPlate("22-BB-22");
        car2.setBatteryCapacity(40.0);
        car2.setOwner(user);

        user.setCars(Arrays.asList(car1, car2));

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.cars.length()").value(2))
                .andExpect(jsonPath("$.cars[0].brand").value("Tesla"))
                .andExpect(jsonPath("$.cars[1].brand").value("Nissan"));
    }
}
