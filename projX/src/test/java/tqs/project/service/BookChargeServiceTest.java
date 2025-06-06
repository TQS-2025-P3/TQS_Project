package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tqs.project.dto.BookChargeDTO;
import tqs.project.model.*;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookChargeServiceTest {

    @Mock
    private BookChargeRepository bookChargeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ChargerStationRepository stationRepository;

    @InjectMocks
    private BookChargeService bookChargeService;

    private User user;
    private Car car;
    private ChargerStation station;
    private BookCharge booking;
    private BookChargeDTO bookingDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João");
        user.setBalance(200.0);

        car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setBatteryCapacity(75.0);
        car.setOwner(user);

        station = new ChargerStation();
        station.setId(1L);
        station.setName("Estação Norte");
        station.setSlots(10);
        station.setSlotsInUse(3);
        station.setPricePerKwh(0.25);

        booking = new BookCharge();
        booking.setId(1L);
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);
        booking.setDuration(60);
        booking.setTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.RESERVED);
        booking.setCost(18.75); // 75 * 0.25

        bookingDTO = new BookChargeDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStationId(1L);
        bookingDTO.setDuration(60);
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso")
    void shouldCreateBookingSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(bookChargeRepository.findByCarIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());
        when(bookChargeRepository.save(any(BookCharge.class))).thenReturn(booking);

        BookCharge result = bookChargeService.createBooking(bookingDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCost()).isEqualTo(18.75);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.RESERVED);
        
        verify(userRepository).findById(1L);
        verify(carRepository).findById(1L);
        verify(stationRepository).findById(1L);
        verify(bookChargeRepository).findByCarIdAndStatus(1L, BookingStatus.RESERVED);
        verify(bookChargeRepository).save(any(BookCharge.class));
        verify(stationRepository).save(station);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção quando utilizador não existe")
    void shouldThrowExceptionWhenUserNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        bookingDTO.setUserId(999L);

        assertThatThrownBy(() -> bookChargeService.createBooking(bookingDTO))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Utilizador não encontrado");

        verify(userRepository).findById(999L);
        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando carro não existe")
    void shouldThrowExceptionWhenCarNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(999L)).thenReturn(Optional.empty());
        bookingDTO.setCarId(999L);

        assertThatThrownBy(() -> bookChargeService.createBooking(bookingDTO))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Carro não encontrado");

        verify(userRepository).findById(1L);
        verify(carRepository).findById(999L);
        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estação não existe")
    void shouldThrowExceptionWhenStationNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());
        bookingDTO.setStationId(999L);

        assertThatThrownBy(() -> bookChargeService.createBooking(bookingDTO))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Estação não encontrada");

        verify(userRepository).findById(1L);
        verify(carRepository).findById(1L);
        verify(stationRepository).findById(999L);
        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando saldo é insuficiente")
    void shouldThrowExceptionWhenBalanceInsufficient() {
        user.setBalance(10.0); // Menos que o custo (18.75)
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        assertThatThrownBy(() -> bookChargeService.createBooking(bookingDTO))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Saldo insuficiente");

        verify(userRepository).findById(1L);
        verify(carRepository).findById(1L);
        verify(stationRepository).findById(1L);
        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe reserva ativa para o carro")
    void shouldThrowExceptionWhenActiveBookingExists() {
        BookCharge existingBooking = new BookCharge();
        existingBooking.setStatus(BookingStatus.RESERVED);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(bookChargeRepository.findByCarIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Arrays.asList(existingBooking));

        assertThatThrownBy(() -> bookChargeService.createBooking(bookingDTO))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Já existe uma reserva ativa para este veículo");

        verify(bookChargeRepository).findByCarIdAndStatus(1L, BookingStatus.RESERVED);
        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há slots disponíveis")
    void shouldThrowExceptionWhenNoSlotsAvailable() {
        station.setSlots(5);
        station.setSlotsInUse(5); // Todos os slots ocupados
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(bookChargeRepository.findByCarIdAndStatus(1L, BookingStatus.RESERVED))
            .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookChargeService.createBooking(bookingDTO))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Não há slots disponíveis na estação selecionada");

        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }

    @Test
    @DisplayName("Deve retornar reservas do utilizador")
    void shouldReturnBookingsByUser() {
        List<BookCharge> bookings = Arrays.asList(booking);
        when(bookChargeRepository.findByUserId(1L)).thenReturn(bookings);

        List<BookCharge> result = bookChargeService.getBookingsByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(booking);
        verify(bookChargeRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Deve atualizar status para COMPLETED e liberar slot")
    void shouldUpdateStatusToCompletedAndReleaseSlot() {
        when(bookChargeRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookChargeRepository.save(any(BookCharge.class))).thenReturn(booking);

        BookCharge result = bookChargeService.updateStatus(1L, "COMPLETED");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        verify(bookChargeRepository).findById(1L);
        verify(bookChargeRepository).save(booking);
        verify(stationRepository).save(station);
    }

    @Test
    @DisplayName("Deve atualizar status para CANCELLED e liberar slot")
    void shouldUpdateStatusToCancelledAndReleaseSlot() {
        when(bookChargeRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookChargeRepository.save(any(BookCharge.class))).thenReturn(booking);

        BookCharge result = bookChargeService.updateStatus(1L, "CANCELLED");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookChargeRepository).findById(1L);
        verify(bookChargeRepository).save(booking);
        verify(stationRepository).save(station);
    }


    @Test
    @DisplayName("Deve atualizar status para RESERVED sem liberar slot")
    void shouldUpdateStatusToReservedWithoutReleasingSlot() throws Exception {
        when(bookChargeRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookChargeRepository.save(any(BookCharge.class))).thenReturn(booking);

        BookCharge result = bookChargeService.updateStatus(1L, "RESERVED");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.RESERVED);
        verify(bookChargeRepository).findById(1L);
        verify(bookChargeRepository).save(booking);
        verify(stationRepository, never()).save(any(ChargerStation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status de reserva inexistente")
    void shouldThrowExceptionWhenUpdatingStatusOfNonExistentBooking() {
        when(bookChargeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookChargeService.updateStatus(999L, "COMPLETED"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Reserva não encontrada");

        verify(bookChargeRepository).findById(999L);
        verify(bookChargeRepository, never()).save(any(BookCharge.class));
    }
}