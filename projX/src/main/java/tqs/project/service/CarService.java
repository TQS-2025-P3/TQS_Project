package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.dto.CarDTO;
import tqs.project.model.Car;
import tqs.project.model.User;
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
        User user = userRepository.findById(carDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPlate(carDTO.getPlate());
        car.setBatteryCapacity(carDTO.getBatteryCapacity());
        car.setOwner(user); // ← garante a associação
    
        return carRepository.save(car);
    }


    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public List<Car> getCarsByUserId(Long userId) {
        return carRepository.findByOwnerId(userId);
    }

    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    public Car updateCar(Long id, CarDTO carDTO) {
        Car car = carRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Carro não encontrado"));
    
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPlate(carDTO.getPlate());
        car.setBatteryCapacity(carDTO.getBatteryCapacity());
    
        return carRepository.save(car);
    }
    
}
