package tqs.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.BookCharge;
import tqs.project.model.enums.BookingStatus;

public interface BookChargeRepository extends JpaRepository<BookCharge, Long> {
    
    List<BookCharge> findByUserId(Long userId);
    List<BookCharge> findByCarIdAndStatus(Long carId, BookingStatus status);
    List<BookCharge> findByTimeAfter(LocalDateTime time);
    List<BookCharge> findByTimeBetween(LocalDateTime start, LocalDateTime end);
    List<BookCharge> findByChargerStationIdAndTimeAfter(Long stationId, LocalDateTime time);
    List<BookCharge> findByChargerStationIdAndTimeBetween(Long stationId, LocalDateTime start, LocalDateTime end);
    List<BookCharge> findByChargerStationId(Long stationId);
    List<BookCharge> findByChargerStationIdAndStatus(Long stationId, BookingStatus status);
    List<BookCharge> findByStatusAndTimeBetween(BookingStatus status, LocalDateTime start, LocalDateTime end);
}