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
                car.setOwner(user); // associação com o novo user
                cars.add(car);
            }
            user.setCars(cars);
        }
    
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
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
                    car.setOwner(user); // associação com o user atualizado
                    cars.add(car);
                }
                user.setCars(cars);
            }

            return userRepository.save(user);
        }
        return null;
    }
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    public List<Car> getCarsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getCars();
        }
        return new ArrayList<>();
    }
}
