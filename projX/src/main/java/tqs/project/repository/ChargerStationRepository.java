package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.ChargerStation;

public interface ChargerStationRepository extends JpaRepository<ChargerStation, Long> {
}
