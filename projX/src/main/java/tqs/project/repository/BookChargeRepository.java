package tqs.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.BookCharge;
import tqs.project.model.enums.BookingStatus;

public interface BookChargeRepository extends JpaRepository<BookCharge, Long> {
    List<BookCharge> findByUserId(Long userId);
    List<BookCharge> findByCarIdAndStatus(Long carId, BookingStatus status);

}



