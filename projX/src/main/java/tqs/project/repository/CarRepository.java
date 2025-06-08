package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwnerId(Long userId);

    boolean existsByPlate(String plate);
    Car findByPlate(String plate);
}
