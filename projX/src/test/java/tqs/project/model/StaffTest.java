package tqs.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class StaffTest {

    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff();
    }

    @Test
    @DisplayName("Deve criar staff com construtor vazio")
    void shouldCreateStaffWithNoArgsConstructor() {
        assertThat(staff).isNotNull();
        assertThat(staff.getStations()).isNull();
    }

    @Test
    @DisplayName("Deve criar staff com todos os argumentos")
    void shouldCreateStaffWithAllArgsConstructor() {
        ChargerStation station = new ChargerStation();
        station.setName("Estação Central");
        
        Staff fullStaff = new Staff(1L, Arrays.asList(station));
        fullStaff.setName("Carlos");
        fullStaff.setEmail("carlos@company.com");
        fullStaff.setPassword("admin123");

        assertThat(fullStaff.getId()).isEqualTo(1L);
        assertThat(fullStaff.getName()).isEqualTo("Carlos");
        assertThat(fullStaff.getEmail()).isEqualTo("carlos@company.com");
        assertThat(fullStaff.getPassword()).isEqualTo("admin123");
        assertThat(fullStaff.getStations()).hasSize(1);
        assertThat(fullStaff.getStations().get(0).getName()).isEqualTo("Estação Central");
    }

    @Test
    @DisplayName("Deve configurar propriedades do staff corretamente")
    void shouldSetStaffPropertiesCorrectly() {
        ChargerStation station1 = new ChargerStation();
        ChargerStation station2 = new ChargerStation();
        
        staff.setId(3L);
        staff.setName("Ana");
        staff.setEmail("ana@company.com");
        staff.setPassword("staff456");
        staff.setStations(Arrays.asList(station1, station2));

        assertThat(staff.getId()).isEqualTo(3L);
        assertThat(staff.getName()).isEqualTo("Ana");
        assertThat(staff.getEmail()).isEqualTo("ana@company.com");
        assertThat(staff.getPassword()).isEqualTo("staff456");
        assertThat(staff.getStations()).hasSize(2);
    }
}