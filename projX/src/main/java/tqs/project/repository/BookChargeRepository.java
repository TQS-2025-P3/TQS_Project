package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.BookCharge;

public interface BookChargeRepository extends JpaRepository<BookCharge, Long> {
}
