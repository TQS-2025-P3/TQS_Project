package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
