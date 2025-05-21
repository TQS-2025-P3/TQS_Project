package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
}
