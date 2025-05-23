package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.dto.CarDTO;
import tqs.project.model.Car;
import tqs.project.repository.CarRepository;
import tqs.project.repository.UserRepository;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car addCar(CarDTO carDTO) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setRangeKm(carDTO.getRangeKm());

        if (carDTO.getOwnerId() != null) {
            userRepository.findById(carDTO.getOwnerId()).ifPresent(car::setOwner);
        }

        return carRepository.save(car);
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }
}
