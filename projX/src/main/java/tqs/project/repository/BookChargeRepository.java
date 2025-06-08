package tqs.project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
    List<BookCharge> findByUserIdAndStatus(Long userId, BookingStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookCharge b WHERE b.car.id = :carId")
    void deleteByCarId(@Param("carId") Long carId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookCharge b WHERE b.chargerStation.id = :stationId")
    void deleteByChargerStationId(@Param("stationId") Long stationId);

    
    @Modifying
    @Transactional
    @Query("DELETE FROM BookCharge b WHERE b.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}