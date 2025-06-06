package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.StaffDTO;
import tqs.project.model.Staff;
import tqs.project.repository.StaffRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

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
    @DisplayName("Deve criar staff com sucesso")
    void shouldCreateStaffSuccessfully() {
        when(staffRepository.save(any(Staff.class))).thenReturn(staff);

        Staff result = staffService.createStaff(staffDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Carlos");
        assertThat(result.getEmail()).isEqualTo("carlos@company.com");
        verify(staffRepository).save(any(Staff.class));
    }

    @Test
    @DisplayName("Deve retornar todos os staff")
    void shouldReturnAllStaff() {
        List<Staff> staffList = Arrays.asList(staff);
        when(staffRepository.findAll()).thenReturn(staffList);

        List<Staff> result = staffService.getAllStaff();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos");
        verify(staffRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar staff por ID")
    void shouldReturnStaffById() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        Staff result = staffService.getStaffById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Carlos");
        verify(staffRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar null quando staff não existe")
    void shouldReturnNullWhenStaffNotExists() {
        when(staffRepository.findById(999L)).thenReturn(Optional.empty());

        Staff result = staffService.getStaffById(999L);

        assertThat(result).isNull();
        verify(staffRepository).findById(999L);
    }
}