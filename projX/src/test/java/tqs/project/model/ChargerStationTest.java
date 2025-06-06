package tqs.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChargerStationTest {

    private ChargerStation station;

    @BeforeEach
    void setUp() {
        station = new ChargerStation();
    }

    @Test
    @DisplayName("Deve criar estação com construtor vazio")
    void shouldCreateStationWithNoArgsConstructor() {
        assertThat(station).isNotNull();
        assertThat(station.getSlotsInUse()).isEqualTo(0);
        assertThat(station.getSlots()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve criar estação com todos os argumentos")
    void shouldCreateStationWithAllArgsConstructor() {
        Staff staff = new Staff();
        staff.setName("Manager");
        
        ChargerStation fullStation = new ChargerStation(
            1L, "Estação Norte", 41.1579, -8.6291, 10, 3, 0.25, staff
        );

        assertThat(fullStation.getId()).isEqualTo(1L);
        assertThat(fullStation.getName()).isEqualTo("Estação Norte");
        assertThat(fullStation.getLatitude()).isEqualTo(41.1579);
        assertThat(fullStation.getLongitude()).isEqualTo(-8.6291);
        assertThat(fullStation.getSlots()).isEqualTo(10);
        assertThat(fullStation.getSlotsInUse()).isEqualTo(3);
        assertThat(fullStation.getPricePerKwh()).isEqualTo(0.25);
        assertThat(fullStation.getStaff().getName()).isEqualTo("Manager");
    }

    @Test
    @DisplayName("Deve calcular slots disponíveis corretamente")
    void shouldCalculateAvailableSlotsCorrectly() {
        station.setSlots(8);
        station.setSlotsInUse(3);

        assertThat(station.getAvailableSlots()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve retornar zero slots disponíveis quando estação lotada")
    void shouldReturnZeroAvailableSlotsWhenFull() {
        station.setSlots(5);
        station.setSlotsInUse(5);

        assertThat(station.getAvailableSlots()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve permitir mais slots em uso que total (caso de erro)")
    void shouldAllowMoreSlotsInUseThanTotal() {
        station.setSlots(3);
        station.setSlotsInUse(5);

        assertThat(station.getAvailableSlots()).isEqualTo(-2);
    }

    @Test
    @DisplayName("Deve configurar coordenadas GPS")
    void shouldSetGPSCoordinates() {
        station.setLatitude(38.7169);
        station.setLongitude(-9.1399);

        assertThat(station.getLatitude()).isEqualTo(38.7169);
        assertThat(station.getLongitude()).isEqualTo(-9.1399);
    }

    @Test
    @DisplayName("Deve configurar preço por kWh")
    void shouldSetPricePerKwh() {
        station.setPricePerKwh(0.30);
        assertThat(station.getPricePerKwh()).isEqualTo(0.30);
    }
}