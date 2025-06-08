package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.project.dto.CarDTO;
import tqs.project.dto.UserDTO;
import tqs.project.model.BookCharge;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.BookChargeRepository;
import tqs.project.repository.CarRepository;
import tqs.project.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookChargeRepository bookChargeRepository;
    
    @Autowired
    private CarRepository carRepository;

    private boolean isDuplicateEmail(String email, Long excludeUserId) {
        List<User> existingUsers = userRepository.findByEmailIgnoreCase(email);
        
        if (excludeUserId != null) {
            existingUsers.removeIf(user -> user.getId().equals(excludeUserId));
        }
        
        return !existingUsers.isEmpty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(UserDTO userDTO) {
        if (isDuplicateEmail(userDTO.getEmail(), null)) {
            throw new RuntimeException("Já existe um utilizador com o email: " + userDTO.getEmail() + 
                                     " (verificação ignora maiúsculas/minúsculas)");
        }
        
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

        if (isDuplicateEmail(userDTO.getEmail(), id)) {
            throw new RuntimeException("Já existe outro utilizador com o email: " + userDTO.getEmail() + 
                                     " (verificação ignora maiúsculas/minúsculas)");
        }

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

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilizador com ID " + id + " não encontrado.");
        }

        List<Car> userCars = carRepository.findByOwnerId(id);
        List<BookCharge> activeBookingsFromCars = new ArrayList<>();
        
        for (Car car : userCars) {
            List<BookCharge> carActiveBookings = bookChargeRepository.findByCarIdAndStatus(car.getId(), BookingStatus.RESERVED);
            activeBookingsFromCars.addAll(carActiveBookings);
        }

        List<BookCharge> directUserBookings = bookChargeRepository.findByUserIdAndStatus(id, BookingStatus.RESERVED);

        int totalActiveBookings = activeBookingsFromCars.size() + directUserBookings.size();

        if (totalActiveBookings > 0) {
            throw new RuntimeException("Não é possível apagar o utilizador. Existem " + 
                                     totalActiveBookings + " reserva(s) ativa(s) associada(s). " +
                                     "Complete ou cancele as reservas primeiro.");
        }

     
        for (Car car : userCars) {
            bookChargeRepository.deleteByCarId(car.getId());
        }
        
        try {
            bookChargeRepository.deleteByUserId(id);
        } catch (Exception e) {
        }

        carRepository.deleteAll(userCars);

        userRepository.deleteById(id);
    }
}