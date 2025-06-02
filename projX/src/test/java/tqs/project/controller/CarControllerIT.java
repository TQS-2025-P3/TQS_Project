package tqs.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.service.CarService;
import tqs.project.dto.CarDTO;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
public class CarControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    @XrayTest(key = "TQSPROJECT-401")
    @Requirement("TQSPROJECT-468")
    @Test
    public void whenPostValidCar_thenCreateCar() throws Exception {
        // Create a valid CarDTO
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Tesla");
        carDTO.setModel("Model 3");
        carDTO.setPlate("AA-11-BB");
        carDTO.setBatteryCapacity(75.0);
        carDTO.setUserId(1L);
        
        // Create expected Car result
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setModel("Model 3");
        car.setPlate("AA-11-BB");
        car.setBatteryCapacity(75.0);
        
        User owner = new User();
        owner.setId(1L);
        car.setOwner(owner);
        
        // Mock service behavior
        when(carService.addCar(any(CarDTO.class))).thenReturn(car);
        
        // Perform POST request and validate
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.brand", is("Tesla")))
                .andExpect(jsonPath("$.model", is("Model 3")))
                .andExpect(jsonPath("$.plate", is("AA-11-BB")))
                .andExpect(jsonPath("$.batteryCapacity", is(75.0)));
    }
}