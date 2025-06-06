package tqs.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tqs.project.model.enums.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookChargeTest {

    private BookCharge booking;
    private User user;
    private Car car;
    private ChargerStation station;

    @BeforeEach
    void setUp() {
        booking = new BookCharge();
        
        user = new User();
        user.setName("João");
        
        car = new Car();
        car.setBrand("Tesla");
        
        station = new ChargerStation();
        station.setName("Estação Central");
    }

    @Test
    @DisplayName("Deve criar reserva com construtor vazio")
    void shouldCreateBookingWithNoArgsConstructor() {
        assertThat(booking).isNotNull();
        assertThat(booking.getCost()).isEqualTo(0.0);
        assertThat(booking.getDuration()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve criar reserva com todos os argumentos")
    void shouldCreateBookingWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        
        BookCharge fullBooking = new BookCharge(
            1L, now, 15.50, 60, BookingStatus.RESERVED, user, car, station
        );

        assertThat(fullBooking.getId()).isEqualTo(1L);
        assertThat(fullBooking.getTime()).isEqualTo(now);
        assertThat(fullBooking.getCost()).isEqualTo(15.50);
        assertThat(fullBooking.getDuration()).isEqualTo(60);
        assertThat(fullBooking.getStatus()).isEqualTo(BookingStatus.RESERVED);
        assertThat(fullBooking.getUser().getName()).isEqualTo("João");
        assertThat(fullBooking.getCar().getBrand()).isEqualTo("Tesla");
        assertThat(fullBooking.getChargerStation().getName()).isEqualTo("Estação Central");
    }

    @Test
    @DisplayName("Deve configurar status da reserva")
    void shouldSetBookingStatus() {
        booking.setStatus(BookingStatus.RESERVED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.RESERVED);
        
        booking.setStatus(BookingStatus.COMPLETED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        
        booking.setStatus(BookingStatus.CANCELLED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Deve configurar tempo da reserva")
    void shouldSetBookingTime() {
        LocalDateTime futureTime = LocalDateTime.of(2024, 12, 25, 14, 30);
        booking.setTime(futureTime);
        
        assertThat(booking.getTime()).isEqualTo(futureTime);
    }

    @Test
    @DisplayName("Deve permitir custo zero")
    void shouldAllowZeroCost() {
        booking.setCost(0.0);
        assertThat(booking.getCost()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Deve permitir duração em minutos")
    void shouldAllowDurationInMinutes() {
        booking.setDuration(120); 
        assertThat(booking.getDuration()).isEqualTo(120);
    }

    @Test
    @DisplayName("Deve associar entidades relacionadas")
    void shouldAssociateRelatedEntities() {
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);

        assertThat(booking.getUser()).isEqualTo(user);
        assertThat(booking.getCar()).isEqualTo(car);
        assertThat(booking.getChargerStation()).isEqualTo(station);
    }
}