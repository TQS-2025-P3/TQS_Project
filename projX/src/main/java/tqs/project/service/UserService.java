package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.dto.CarDTO;
import tqs.project.dto.UserDTO;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
    
        if (userDTO.getCars() != null) {
            List<Car> cars = new ArrayList<>();
            for (CarDTO carDTO : userDTO.getCars()) {
                Car car = new Car();
                car.setBrand(carDTO.getBrand());
                car.setModel(carDTO.getModel());
                car.setPlate(carDTO.getPlate());
                car.setBatteryCapacity(carDTO.getBatteryCapacity());
                car.setOwner(user);
                cars.add(car);
            }
            user.setCars(cars);
        }
    
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilizador com ID " + id + " não encontrado."));

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(userDTO.getPassword());
        }

        if (userDTO.getBalance() != null) {
            existingUser.setBalance(userDTO.getBalance());
        }

        if (userDTO.getCars() != null) {
            List<Car> cars = new ArrayList<>();
            for (CarDTO carDTO : userDTO.getCars()) {
                Car car = new Car();
                car.setBrand(carDTO.getBrand());
                car.setModel(carDTO.getModel());
                car.setPlate(carDTO.getPlate());
                car.setBatteryCapacity(carDTO.getBatteryCapacity());
                car.setOwner(existingUser);
                cars.add(car);
            }
            existingUser.setCars(cars);
        }

        return userRepository.save(existingUser);
    }
   
    public List<Car> getCarsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getCars();
        }
        return new ArrayList<>();
    }

    public User addFunds(Long userId, double amount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    
        if (amount < 0) {
            throw new IllegalArgumentException("Montante inválido.");
        }
    
        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }

    public Double getUserBalance(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        return user.getBalance();
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilizador com ID " + id + " não encontrado.");
        }
        userRepository.deleteById(id);
    }
}