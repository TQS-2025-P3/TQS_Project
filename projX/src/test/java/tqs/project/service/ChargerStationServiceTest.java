package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.ChargerStationDTO;
import tqs.project.model.BookCharge;
import tqs.project.model.ChargerStation;
import tqs.project.model.Staff;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.BookChargeRepository;
import tqs.project.repository.ChargerStationRepository;
import tqs.project.repository.StaffRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargerStationServiceTest {

    @Mock
    private ChargerStationRepository stationRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private BookChargeRepository bookChargeRepository;

    @InjectMocks
    private ChargerStationService stationService;

    private ChargerStation station;
    private Staff staff;
    private ChargerStationDTO stationDTO;

    @BeforeEach
    void setUp() {
        staff = new Staff();
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
    @DisplayName("Deve retornar todas as estações")
    void shouldReturnAllStations() {
        List<ChargerStation> stations = Arrays.asList(station);
        when(stationRepository.findAll()).thenReturn(stations);

        List<ChargerStation> result = stationService.getAllStations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Estação Norte");
        verify(stationRepository).findAll();
    }

    @Test
    @DisplayName("Deve criar estação com sucesso")
    void shouldCreateStationSuccessfully() {
        when(stationRepository.findByNameIgnoreCase("Estação Norte")).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(41.1579, -8.6291)).thenReturn(null);
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(stationRepository.save(any(ChargerStation.class))).thenReturn(station);

        ChargerStation result = stationService.createStation(stationDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Estação Norte");
        verify(staffRepository).findById(1L);
        verify(stationRepository).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar estação com nome duplicado")
    void shouldThrowExceptionWhenCreatingStationWithDuplicateName() {
        ChargerStation existingStation = new ChargerStation();
        existingStation.setId(2L);
        existingStation.setName("Estação Norte");
        
        when(stationRepository.findByNameIgnoreCase("Estação Norte"))
            .thenReturn(new java.util.ArrayList<>(Arrays.asList(existingStation)));

        assertThatThrownBy(() -> stationService.createStation(stationDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe uma estação com o nome: Estação Norte");

        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar estação com coordenadas duplicadas")
    void shouldThrowExceptionWhenCreatingStationWithDuplicateCoordinates() {
        ChargerStation existingStation = new ChargerStation();
        existingStation.setId(2L);
        existingStation.setLatitude(41.1579);
        existingStation.setLongitude(-8.6291);

        when(stationRepository.findByNameIgnoreCase("Estação Norte")).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(41.1579, -8.6291)).thenReturn(existingStation);

        assertThatThrownBy(() -> stationService.createStation(stationDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe uma estação nas coordenadas: 41.1579, -8.6291");

        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar estação com staff inexistente")
    void shouldThrowExceptionWhenCreatingStationWithNonExistentStaff() {
        when(stationRepository.findByNameIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(anyDouble(), anyDouble())).thenReturn(null);
        when(staffRepository.findById(999L)).thenReturn(Optional.empty());
        stationDTO.setStaffId(999L);

        assertThatThrownBy(() -> stationService.createStation(stationDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Staff com ID 999 não existe");

        verify(staffRepository).findById(999L);
        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve atualizar estação existente")
    void shouldUpdateExistingStation() {
        ChargerStationDTO updateDTO = new ChargerStationDTO();
        updateDTO.setName("Estação Sul");
        updateDTO.setLatitude(38.7169);
        updateDTO.setLongitude(-9.1399);
        updateDTO.setSlots(8);
        updateDTO.setPricePerKwh(0.30);
        updateDTO.setStaffId(1L);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.findByNameIgnoreCase("Estação Sul")).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(38.7169, -9.1399)).thenReturn(null);
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(stationRepository.save(any(ChargerStation.class))).thenReturn(station);

        ChargerStation result = stationService.updateStation(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(stationRepository).findById(1L);
        verify(staffRepository).findById(1L);
        verify(stationRepository).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar estação com o mesmo nome")
    void shouldAllowUpdateStationWithSameName() {
        ChargerStationDTO updateDTO = new ChargerStationDTO();
        updateDTO.setName("Estação Norte"); 
        updateDTO.setLatitude(38.7169);
        updateDTO.setLongitude(-9.1399);
        updateDTO.setSlots(8);
        updateDTO.setPricePerKwh(0.30);
        updateDTO.setStaffId(1L);

        ChargerStation existingStationWithSameName = new ChargerStation();
        existingStationWithSameName.setId(1L); 
        existingStationWithSameName.setName("Estação Norte");

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.findByNameIgnoreCase("Estação Norte"))
            .thenReturn(new java.util.ArrayList<>(Arrays.asList(existingStationWithSameName)));
        when(stationRepository.findByLatitudeAndLongitude(38.7169, -9.1399)).thenReturn(null);
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(stationRepository.save(any(ChargerStation.class))).thenReturn(station);

        ChargerStation result = stationService.updateStation(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(stationRepository).findById(1L);
        verify(stationRepository).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve permitir atualizar estação com as mesmas coordenadas")
    void shouldAllowUpdateStationWithSameCoordinates() {
        ChargerStationDTO updateDTO = new ChargerStationDTO();
        updateDTO.setName("Estação Sul");
        updateDTO.setLatitude(41.1579); 
        updateDTO.setLongitude(-8.6291);
        updateDTO.setSlots(8);
        updateDTO.setPricePerKwh(0.30);
        updateDTO.setStaffId(1L);

        ChargerStation existingStationWithSameCoords = new ChargerStation();
        existingStationWithSameCoords.setId(1L); 
        existingStationWithSameCoords.setLatitude(41.1579);
        existingStationWithSameCoords.setLongitude(-8.6291);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.findByNameIgnoreCase("Estação Sul")).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(41.1579, -8.6291)).thenReturn(existingStationWithSameCoords);
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(stationRepository.save(any(ChargerStation.class))).thenReturn(station);

        ChargerStation result = stationService.updateStation(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(stationRepository).findById(1L);
        verify(stationRepository).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estação com nome duplicado de outra estação")
    void shouldThrowExceptionWhenUpdatingStationWithDuplicateNameFromOtherStation() {
        ChargerStationDTO updateDTO = new ChargerStationDTO();
        updateDTO.setName("Estação Existente");
        updateDTO.setLatitude(38.7169);
        updateDTO.setLongitude(-9.1399);
        updateDTO.setSlots(8);
        updateDTO.setPricePerKwh(0.30);
        updateDTO.setStaffId(1L);

        ChargerStation otherStationWithSameName = new ChargerStation();
        otherStationWithSameName.setId(2L); 
        otherStationWithSameName.setName("Estação Existente");

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.findByNameIgnoreCase("Estação Existente"))
            .thenReturn(new java.util.ArrayList<>(Arrays.asList(otherStationWithSameName)));

        assertThatThrownBy(() -> stationService.updateStation(1L, updateDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe outra estação com o nome: Estação Existente");

        verify(stationRepository).findById(1L);
        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estação com coordenadas duplicadas de outra estação")
    void shouldThrowExceptionWhenUpdatingStationWithDuplicateCoordinatesFromOtherStation() {
        ChargerStationDTO updateDTO = new ChargerStationDTO();
        updateDTO.setName("Estação Sul");
        updateDTO.setLatitude(38.7169);
        updateDTO.setLongitude(-9.1399);
        updateDTO.setSlots(8);
        updateDTO.setPricePerKwh(0.30);
        updateDTO.setStaffId(1L);

        ChargerStation otherStationWithSameCoords = new ChargerStation();
        otherStationWithSameCoords.setId(2L);
        otherStationWithSameCoords.setLatitude(38.7169);
        otherStationWithSameCoords.setLongitude(-9.1399);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.findByNameIgnoreCase("Estação Sul")).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(38.7169, -9.1399)).thenReturn(otherStationWithSameCoords);

        assertThatThrownBy(() -> stationService.updateStation(1L, updateDTO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Já existe outra estação nas coordenadas: 38.7169, -9.1399");

        verify(stationRepository).findById(1L);
        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estação inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentStation() {
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stationService.updateStation(999L, stationDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Estação com ID 999 não encontrada");

        verify(stationRepository).findById(999L);
        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estação com staff inexistente")
    void shouldThrowExceptionWhenUpdatingStationWithNonExistentStaff() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.findByNameIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(stationRepository.findByLatitudeAndLongitude(anyDouble(), anyDouble())).thenReturn(null);
        when(staffRepository.findById(999L)).thenReturn(Optional.empty());
        stationDTO.setStaffId(999L);

        assertThatThrownBy(() -> stationService.updateStation(1L, stationDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Staff com ID 999 não existe");

        verify(stationRepository).findById(1L);
        verify(staffRepository).findById(999L);
        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve eliminar estação existente")
    void shouldDeleteExistingStation() {
        when(stationRepository.existsById(1L)).thenReturn(true);
        when(bookChargeRepository.findByChargerStationIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());

        stationService.deleteStation(1L);

        verify(stationRepository).existsById(1L);
        verify(bookChargeRepository).findByChargerStationIdAndStatus(1L, BookingStatus.RESERVED);
        verify(bookChargeRepository).deleteByChargerStationId(1L);
        verify(stationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao eliminar estação com reservas ativas")
    void shouldThrowExceptionWhenDeletingStationWithActiveBookings() {
        BookCharge activeBooking = new BookCharge();
        activeBooking.setId(100L);

        when(stationRepository.existsById(1L)).thenReturn(true);
        when(bookChargeRepository.findByChargerStationIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Arrays.asList(activeBooking));

        assertThatThrownBy(() -> stationService.deleteStation(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Não é possível apagar a estação. Existem 1 reserva(s) ativa(s)");

        verify(stationRepository).existsById(1L);
        verify(bookChargeRepository).findByChargerStationIdAndStatus(1L, BookingStatus.RESERVED);
        verify(stationRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao eliminar estação inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentStation() {
        when(stationRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> stationService.deleteStation(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Estação com ID 999 não encontrada");

        verify(stationRepository).existsById(999L);
        verify(stationRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve retornar estação por ID")
    void shouldReturnStationById() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        ChargerStation result = stationService.getStationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Estação Norte");
        verify(stationRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter estação inexistente")
    void shouldThrowExceptionWhenGettingNonExistentStation() {
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stationService.getStationById(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Estação com ID 999 não encontrada");

        verify(stationRepository).findById(999L);
    }
}