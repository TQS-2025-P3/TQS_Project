package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tqs.project.model.ChargerStation;

import java.util.List;

public interface ChargerStationRepository extends JpaRepository<ChargerStation, Long> {
    
    @Query("SELECT c FROM ChargerStation c WHERE UPPER(c.name) = UPPER(:name)")
    List<ChargerStation> findByNameIgnoreCase(@Param("name") String name);
    
    ChargerStation findByLatitudeAndLongitude(double latitude, double longitude);
    
    List<ChargerStation> findAll();
}