package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tqs.project.dto.CarDTO;
import tqs.project.model.BookCharge;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.BookChargeRepository;
import tqs.project.repository.CarRepository;
import tqs.project.repository.UserRepository;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookChargeRepository bookChargeRepository;

    private String normalizePlate(String plate) {
        if (plate == null) return null;
        return plate.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    private boolean isDuplicatePlate(String plate, Long excludeCarId) {
        String normalizedInput = normalizePlate(plate);
        
        List<Car> allCars = carRepository.findAll();
        
        for (Car car : allCars) {
            if (excludeCarId != null && car.getId().equals(excludeCarId)) {
                continue;
            }
            
            String existingNormalized = normalizePlate(car.getPlate());
            if (normalizedInput.equals(existingNormalized)) {
                return true;
            }
        }
        
        return false;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car addCar(CarDTO carDTO) {
        if (isDuplicatePlate(carDTO.getPlate(), null)) {
            throw new RuntimeException("Já existe um carro com a matrícula: " + carDTO.getPlate() + 
                                     " (verificação inclui formatos diferentes como hífens e espaços)");
        }
        
        User user = userRepository.findById(carDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPlate(carDTO.getPlate()); 
        car.setBatteryCapacity(carDTO.getBatteryCapacity());
        car.setOwner(user);
    
        return carRepository.save(car);
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public List<Car> getCarsByUserId(Long userId) {
        return carRepository.findByOwnerId(userId);
    }

    @Transactional
    public void deleteCarById(Long id) {
        List<BookCharge> activeBookings = bookChargeRepository.findByCarIdAndStatus(id, BookingStatus.RESERVED);
        
        if (!activeBookings.isEmpty()) {
            throw new RuntimeException("Não é possível apagar o carro. Existem " + 
                                     activeBookings.size() + " reserva(s) ativa(s).");
        }
        
        bookChargeRepository.deleteByCarId(id);
        carRepository.deleteById(id);
    }

    public Car updateCar(Long id, CarDTO carDTO) {
        Car car = carRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Carro não encontrado"));

        if (isDuplicatePlate(carDTO.getPlate(), id)) {
            throw new RuntimeException("Já existe outro carro com a matrícula: " + carDTO.getPlate() + 
                                     " (verificação inclui formatos diferentes como hífens e espaços)");
        }
    
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPlate(carDTO.getPlate());
        car.setBatteryCapacity(carDTO.getBatteryCapacity());
    
        return carRepository.save(car);
    }
}