package tqs.project.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tqs.project.model.*;
import tqs.project.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookChargeRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookChargeRepository bookChargeRepository;

    private User user;
    private Car car;
    private ChargerStation station;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("João");
        user.setEmail("joao@test.com");
        user.setPassword("pass");
        user.setBalance(100.0);
        user = entityManager.persistAndFlush(user);

        car = new Car();
        car.setBrand("Tesla");
        car.setModel("Model 3");
        car.setPlate("AB-12-CD");
        car.setBatteryCapacity(75.0);
        car.setOwner(user);
        car = entityManager.persistAndFlush(car);

        Staff staff = new Staff();
        staff.setName("Admin");
        staff.setEmail("admin@test.com");
        staff.setPassword("admin");
        staff = entityManager.persistAndFlush(staff);

        station = new ChargerStation();
        station.setName("Estação Norte");
        station.setLatitude(41.1579);
        station.setLongitude(-8.6291);
        station.setSlots(10);
        station.setSlotsInUse(0);
        station.setPricePerKwh(0.25);
        station.setStaff(staff);
        station = entityManager.persistAndFlush(station);
    }

    @Test
    @DisplayName("Deve encontrar reservas por utilizador")
    void shouldFindBookingsByUser() {
        BookCharge booking = new BookCharge();
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);
        booking.setDuration(60);
        booking.setTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.RESERVED);
        booking.setCost(18.75);
        entityManager.persistAndFlush(booking);

        List<BookCharge> found = bookChargeRepository.findByUserId(user.getId());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDuration()).isEqualTo(60);
        assertThat(found.get(0).getStatus()).isEqualTo(BookingStatus.RESERVED);
    }

    @Test
    @DisplayName("Deve encontrar reservas por carro e status")
    void shouldFindBookingsByCarAndStatus() {
        BookCharge booking = new BookCharge();
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);
        booking.setDuration(60);
        booking.setTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.RESERVED);
        booking.setCost(18.75);
        entityManager.persistAndFlush(booking);

        List<BookCharge> found = bookChargeRepository.findByCarIdAndStatus(car.getId(), BookingStatus.RESERVED);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCar().getPlate()).isEqualTo("AB-12-CD");
    }

    @Test
    @DisplayName("Deve encontrar reservas por período de tempo")
    void shouldFindBookingsByTimeRange() {
        LocalDateTime now = LocalDateTime.now();
        
        BookCharge booking = new BookCharge();
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);
        booking.setDuration(60);
        booking.setTime(now);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCost(18.75);
        entityManager.persistAndFlush(booking);

        List<BookCharge> found = bookChargeRepository.findByTimeBetween(
            now.minusHours(1), 
            now.plusHours(1)
        );

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getStatus()).isEqualTo(BookingStatus.COMPLETED);
    }
}