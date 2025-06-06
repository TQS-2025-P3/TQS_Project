package tqs.project.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DTOTest {

    @Test
    @DisplayName("Deve criar e configurar UserDTO corretamente")
    void shouldCreateAndConfigureUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("João");
        userDTO.setEmail("joao@test.com");
        userDTO.setPassword("password123");
        userDTO.setBalance(100.0);

        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Tesla");
        carDTO.setModel("Model 3");
        carDTO.setPlate("AB-12-CD");
        carDTO.setBatteryCapacity(75.0);
        userDTO.setCars(Arrays.asList(carDTO));

        assertThat(userDTO.getName()).isEqualTo("João");
        assertThat(userDTO.getEmail()).isEqualTo("joao@test.com");
        assertThat(userDTO.getPassword()).isEqualTo("password123");
        assertThat(userDTO.getBalance()).isEqualTo(100.0);
        assertThat(userDTO.getCars()).hasSize(1);
        assertThat(userDTO.getCars().get(0).getBrand()).isEqualTo("Tesla");
    }

    @Test
    @DisplayName("Deve criar e configurar CarDTO corretamente")
    void shouldCreateAndConfigureCarDTO() {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("BMW");
        carDTO.setModel("i3");
        carDTO.setPlate("XY-34-ZW");
        carDTO.setBatteryCapacity(42.2);
        carDTO.setUserId(1L);

        assertThat(carDTO.getBrand()).isEqualTo("BMW");
        assertThat(carDTO.getModel()).isEqualTo("i3");
        assertThat(carDTO.getPlate()).isEqualTo("XY-34-ZW");
        assertThat(carDTO.getBatteryCapacity()).isEqualTo(42.2);
        assertThat(carDTO.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve criar e configurar StaffDTO corretamente")
    void shouldCreateAndConfigureStaffDTO() {
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setName("Carlos");
        staffDTO.setEmail("carlos@company.com");
        staffDTO.setPassword("admin123");

        assertThat(staffDTO.getName()).isEqualTo("Carlos");
        assertThat(staffDTO.getEmail()).isEqualTo("carlos@company.com");
        assertThat(staffDTO.getPassword()).isEqualTo("admin123");
    }

    @Test
    @DisplayName("Deve criar e configurar ChargerStationDTO corretamente")
    void shouldCreateAndConfigureChargerStationDTO() {
        ChargerStationDTO stationDTO = new ChargerStationDTO();
        stationDTO.setName("Estação Norte");
        stationDTO.setLatitude(41.1579);
        stationDTO.setLongitude(-8.6291);
        stationDTO.setSlots(10);
        stationDTO.setPricePerKwh(0.25);
        stationDTO.setStaffId(1L);

        assertThat(stationDTO.getName()).isEqualTo("Estação Norte");
        assertThat(stationDTO.getLatitude()).isEqualTo(41.1579);
        assertThat(stationDTO.getLongitude()).isEqualTo(-8.6291);
        assertThat(stationDTO.getSlots()).isEqualTo(10);
        assertThat(stationDTO.getPricePerKwh()).isEqualTo(0.25);
        assertThat(stationDTO.getStaffId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve criar e configurar BookChargeDTO corretamente")
    void shouldCreateAndConfigureBookChargeDTO() {
        BookChargeDTO bookingDTO = new BookChargeDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(2L);
        bookingDTO.setStationId(3L);
        bookingDTO.setDuration(60);

        assertThat(bookingDTO.getUserId()).isEqualTo(1L);
        assertThat(bookingDTO.getCarId()).isEqualTo(2L);
        assertThat(bookingDTO.getStationId()).isEqualTo(3L);
        assertThat(bookingDTO.getDuration()).isEqualTo(60);
    }

    @Test
    @DisplayName("Deve criar e configurar RushHourData corretamente")
    void shouldCreateAndConfigureRushHourData() {
        RushHourData data = new RushHourData("08:00", 25);

        assertThat(data.getHour()).isEqualTo("08:00");
        assertThat(data.getReservations()).isEqualTo(25); 
    }

    @Test
    @DisplayName("Deve criar e configurar WeeklyTrendData corretamente")
    void shouldCreateAndConfigureWeeklyTrendData() {
        WeeklyTrendData data = new WeeklyTrendData("01/06", 120, 115);

        assertThat(data.getDate()).isEqualTo("01/06");
        assertThat(data.getReservations()).isEqualTo(120); 
        assertThat(data.getCompleted()).isEqualTo(115); 
    }

    @Test
    @DisplayName("Deve criar e configurar MonthlyRevenueData corretamente")
    void shouldCreateAndConfigureMonthlyRevenueData() {
        MonthlyRevenueData data = new MonthlyRevenueData("Mai", 3500.0, 350);

        assertThat(data.getMonth()).isEqualTo("Mai");
        assertThat(data.getRevenue()).isEqualTo(3500.0);
        assertThat(data.getSessions()).isEqualTo(350);
    }
}