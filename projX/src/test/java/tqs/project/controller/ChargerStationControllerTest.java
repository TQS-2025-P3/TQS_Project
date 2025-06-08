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
import tqs.project.dto.ChargerStationDTO;
import tqs.project.model.ChargerStation;
import tqs.project.model.Staff;
import tqs.project.service.ChargerStationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChargerStationController.class)
class ChargerStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChargerStationService stationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChargerStation station;
    private ChargerStationDTO stationDTO;

    @BeforeEach
    void setUp() {
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("Carlos");

        station = new ChargerStation();
        station.setId(1L);
        station.setName("Estação Norte");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setSlots(10);
        station.setSlotsInUse(3);
        station.setPricePerKwh(0.25);
        station.setStaff(staff);

        stationDTO = new ChargerStationDTO();
        stationDTO.setName("Estação Norte");
        stationDTO.setLatitude(41.1579);
        stationDTO.setLongitude(-8.6291);
        stationDTO.setSlots(10);
        stationDTO.setPricePerKwh(0.25);
        stationDTO.setStaffId(1L);
    }

    @Test
    @DisplayName("GET /api/stations - Deve retornar todas as estações")
    void shouldReturnAllStations() throws Exception {
        List<ChargerStation> stations = Arrays.asList(station);
        when(stationService.getAllStations()).thenReturn(stations);

        mockMvc.perform(get("/api/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Estação Norte"))
                .andExpect(jsonPath("$[0].latitude").value(41.1579))
                .andExpect(jsonPath("$[0].slots").value(10))
                .andExpect(jsonPath("$[0].pricePerKwh").value(0.25));

        verify(stationService).getAllStations();
    }

    @Test
    @DisplayName("POST /api/stations - Deve criar estação com sucesso")
    void shouldCreateStationSuccessfully() throws Exception {
        when(stationService.createStation(any(ChargerStationDTO.class))).thenReturn(station);

        mockMvc.perform(post("/api/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Estação Norte"))
                .andExpect(jsonPath("$.latitude").value(41.1579))
                .andExpect(jsonPath("$.pricePerKwh").value(0.25));

        verify(stationService).createStation(any(ChargerStationDTO.class));
    }

    @Test
    @DisplayName("POST /api/stations - Deve retornar erro quando criar estação falha")
    void shouldReturnErrorWhenCreateStationFails() throws Exception {
        when(stationService.createStation(any(ChargerStationDTO.class)))
            .thenThrow(new RuntimeException("Já existe uma estação com o nome: Estação Norte"));

        mockMvc.perform(post("/api/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Já existe uma estação com o nome: Estação Norte"));

        verify(stationService).createStation(any(ChargerStationDTO.class));
    }

    @Test
    @DisplayName("PUT /api/stations/{id} - Deve atualizar estação")
    void shouldUpdateStation() throws Exception {
        when(stationService.updateStation(eq(1L), any(ChargerStationDTO.class))).thenReturn(station);

        mockMvc.perform(put("/api/stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Estação Norte"))
                .andExpect(jsonPath("$.latitude").value(41.1579));

        verify(stationService).updateStation(eq(1L), any(ChargerStationDTO.class));
    }

    @Test
    @DisplayName("PUT /api/stations/{id} - Deve retornar 404 quando estação não existe para atualizar")
    void shouldReturn404WhenUpdatingNonExistentStation() throws Exception {
        when(stationService.updateStation(eq(999L), any(ChargerStationDTO.class)))
            .thenThrow(new IllegalArgumentException("Estação com ID 999 não encontrada"));

        mockMvc.perform(put("/api/stations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isNotFound());

        verify(stationService).updateStation(eq(999L), any(ChargerStationDTO.class));
    }

    @Test
    @DisplayName("PUT /api/stations/{id} - Deve retornar erro quando atualizar estação falha")
    void shouldReturnErrorWhenUpdateStationFails() throws Exception {
        when(stationService.updateStation(eq(1L), any(ChargerStationDTO.class)))
            .thenThrow(new RuntimeException("Já existe outra estação com o nome: Estação Norte"));

        mockMvc.perform(put("/api/stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Já existe outra estação com o nome: Estação Norte"));

        verify(stationService).updateStation(eq(1L), any(ChargerStationDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/stations/{id} - Deve eliminar estação com sucesso")
    void shouldDeleteStationSuccessfully() throws Exception {
        doNothing().when(stationService).deleteStation(1L);

        mockMvc.perform(delete("/api/stations/1"))
                .andExpect(status().isOk());

        verify(stationService).deleteStation(1L);
    }

    @Test
    @DisplayName("DELETE /api/stations/{id} - Deve retornar 404 quando estação não existe")
    void shouldReturn404WhenStationNotExists() throws Exception {
        doThrow(new IllegalArgumentException("Estação não encontrada"))
            .when(stationService).deleteStation(999L);

        mockMvc.perform(delete("/api/stations/999"))
                .andExpect(status().isNotFound());

        verify(stationService).deleteStation(999L);
    }

    @Test
    @DisplayName("DELETE /api/stations/{id} - Deve retornar erro quando eliminar estação falha")
    void shouldReturnErrorWhenDeleteStationFails() throws Exception {
        doThrow(new RuntimeException("Não é possível apagar a estação. Existem reservas ativas"))
            .when(stationService).deleteStation(1L);

        mockMvc.perform(delete("/api/stations/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Não é possível apagar a estação. Existem reservas ativas"));

        verify(stationService).deleteStation(1L);
    }

    @Test
    @DisplayName("GET /api/stations/{id} - Deve retornar estação por ID")
    void shouldReturnStationById() throws Exception {
        when(stationService.getStationById(1L)).thenReturn(station);

        mockMvc.perform(get("/api/stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Estação Norte"))
                .andExpect(jsonPath("$.latitude").value(41.1579))
                .andExpect(jsonPath("$.longitude").value(-8.6291));

        verify(stationService).getStationById(1L);
    }

    @Test
    @DisplayName("GET /api/stations/{id} - Deve retornar 404 quando estação não existe para obter")
    void shouldReturn404WhenGettingNonExistentStation() throws Exception {
        when(stationService.getStationById(999L))
            .thenThrow(new IllegalArgumentException("Estação com ID 999 não encontrada"));

        mockMvc.perform(get("/api/stations/999"))
                .andExpect(status().isNotFound());

        verify(stationService).getStationById(999L);
    }
}