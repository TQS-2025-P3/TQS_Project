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
import tqs.project.dto.StaffDTO;
import tqs.project.model.Staff;
import tqs.project.service.StaffService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaffController.class)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffService staffService;

    @Autowired
    private ObjectMapper objectMapper;

    private Staff staff;
    private StaffDTO staffDTO;

    @BeforeEach
    void setUp() {
        staff = new Staff();
        staff.setId(1L);
        staff.setName("Carlos");
        staff.setEmail("carlos@company.com");
        staff.setPassword("admin123");

        staffDTO = new StaffDTO();
        staffDTO.setName("Carlos");
        staffDTO.setEmail("carlos@company.com");
        staffDTO.setPassword("admin123");
    }

    @Test
    @DisplayName("POST /api/staffs - Deve criar staff com sucesso")
    void shouldCreateStaffSuccessfully() throws Exception {
        when(staffService.createStaff(any(StaffDTO.class))).thenReturn(staff);

        mockMvc.perform(post("/api/staffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staffDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@company.com"));

        verify(staffService).createStaff(any(StaffDTO.class));
    }

    @Test
    @DisplayName("GET /api/staffs - Deve retornar todos os staff")
    void shouldReturnAllStaff() throws Exception {
        List<Staff> staffList = Arrays.asList(staff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        mockMvc.perform(get("/api/staffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Carlos"))
                .andExpect(jsonPath("$[0].email").value("carlos@company.com"));

        verify(staffService).getAllStaff();
    }

    @Test
    @DisplayName("GET /api/staffs/{id} - Deve retornar staff por ID")
    void shouldReturnStaffById() throws Exception {
        when(staffService.getStaffById(1L)).thenReturn(staff);

        mockMvc.perform(get("/api/staffs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@company.com"));

        verify(staffService).getStaffById(1L);
    }
}