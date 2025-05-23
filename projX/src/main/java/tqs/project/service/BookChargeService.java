package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.dto.BookChargeDTO;
import tqs.project.exception.ResourceNotFoundException;
import tqs.project.model.BookCharge;
import tqs.project.model.Car;
import tqs.project.model.ChargerStation;
import tqs.project.model.User;
import tqs.project.repository.BookChargeRepository;
import tqs.project.repository.CarRepository;
import tqs.project.repository.ChargerStationRepository;
import tqs.project.repository.UserRepository;

import java.util.List;

@Service
public class BookChargeService {

    @Autowired
    private BookChargeRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargerStationRepository stationRepository;

    public List<BookCharge> getAllBookings() {
        return bookingRepository.findAll();
    }

    public BookCharge createBooking(BookChargeDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador com ID " + dto.getUserId() + " não existe."));

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Carro com ID " + dto.getCarId() + " não existe."));

        ChargerStation station = stationRepository.findById(dto.getChargerStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Estação com ID " + dto.getChargerStationId() + " não existe."));

        BookCharge booking = new BookCharge();
        booking.setTime(dto.getTime());
        booking.setDuration(dto.getDuration());
        booking.setStatus(dto.getStatus());
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);

        return bookingRepository.save(booking);
    }
}
