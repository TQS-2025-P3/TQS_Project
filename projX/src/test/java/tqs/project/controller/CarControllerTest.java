package tqs.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.dto.CarDTO;
import tqs.project.model.Car;
import tqs.project.service.CarService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    private Car car;
    private CarDTO carDTO;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setModel("Model 3");
        car.setPlate("AB-12-CD");
        car.setBatteryCapacity(75.0);

        carDTO = new CarDTO();
        carDTO.setBrand("Tesla");
        carDTO.setModel("Model 3");
        carDTO.setPlate("AB-12-CD");
        carDTO.setBatteryCapacity(75.0);
        carDTO.setUserId(1L);
    }

    @Test
    @DisplayName("GET /api/cars - Deve retornar todos os carros")
    void shouldReturnAllCars() throws Exception {
        List<Car> cars = Arrays.asList(car);
        when(carService.getAllCars()).thenReturn(cars);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].brand").value("Tesla"))
                .andExpect(jsonPath("$[0].model").value("Model 3"))
                .andExpect(jsonPath("$[0].plate").value("AB-12-CD"));

        verify(carService).getAllCars();
    }

    @Test
    @DisplayName("POST /api/cars - Deve criar carro com sucesso")
    void shouldCreateCarSuccessfully() throws Exception {
        when(carService.addCar(any(CarDTO.class))).thenReturn(car);

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model 3"))
                .andExpect(jsonPath("$.plate").value("AB-12-CD"));

        verify(carService).addCar(any(CarDTO.class));
    }

    @Test
    @DisplayName("GET /api/cars/{id} - Deve retornar carro por ID")
    void shouldReturnCarById() throws Exception {
        when(carService.getCarById(1L)).thenReturn(car);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model 3"))
                .andExpect(jsonPath("$.plate").value("AB-12-CD"));

        verify(carService).getCarById(1L);
    }

    @Test
    @DisplayName("GET /api/cars/user/{userId} - Deve retornar carros do utilizador")
    void shouldReturnCarsByUser() throws Exception {
        List<Car> cars = Arrays.asList(car);
        when(carService.getCarsByUserId(1L)).thenReturn(cars);

        mockMvc.perform(get("/api/cars/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].brand").value("Tesla"))
                .andExpect(jsonPath("$[0].model").value("Model 3"));

        verify(carService).getCarsByUserId(1L);
    }

    @Test
    @DisplayName("DELETE /api/cars/{id} - Deve eliminar carro")
    void shouldDeleteCar() throws Exception {
        doNothing().when(carService).deleteCarById(1L);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isOk());

        verify(carService).deleteCarById(1L);
    }

    @Test
    @DisplayName("PUT /api/cars/{id} - Deve atualizar carro")
    void shouldUpdateCar() throws Exception {
        when(carService.updateCar(eq(1L), any(CarDTO.class))).thenReturn(car);

        mockMvc.perform(put("/api/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model 3"));

        verify(carService).updateCar(eq(1L), any(CarDTO.class));
    }
}