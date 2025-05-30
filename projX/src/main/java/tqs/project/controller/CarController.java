package tqs.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.CarDTO;
import tqs.project.model.Car;
import tqs.project.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://localhost:3000")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @PostMapping
    public Car createCar(@Valid @RequestBody CarDTO carDTO) {
        return carService.addCar(carDTO);
    }

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Car> getCarsByUser(@PathVariable Long userId) {
    return carService.getCarsByUserId(userId);
}

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCarById(id);
    }

    @PutMapping("/{id}")
    public Car updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
    return carService.updateCar(id, carDTO);
}
}
