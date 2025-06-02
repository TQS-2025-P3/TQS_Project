package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.dto.BookChargeDTO;
import tqs.project.model.*;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class BookChargeService {

    @Autowired
    private BookChargeRepository bookChargeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargerStationRepository stationRepository;

    public BookCharge createBooking(BookChargeDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilizador não encontrado"));
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));
        ChargerStation station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estação não encontrada"));
    
        double cost = car.getBatteryCapacity() * station.getPricePerKwh();
    
        if (user.getBalance() < cost) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Saldo insuficiente");
        }
    
        List<BookCharge> existing = bookChargeRepository.findByCarIdAndStatus(dto.getCarId(), BookingStatus.RESERVED);
        if (!existing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma reserva ativa para este veículo");
        }
    
        if (station.getAvailableSlots() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não há slots disponíveis na estação selecionada");
        }
    
        BookCharge booking = new BookCharge();
        booking.setUser(user);
        booking.setCar(car);
        booking.setChargerStation(station);
        booking.setDuration(dto.getDuration());
        booking.setTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.RESERVED);
        booking.setCost(cost); // 👈 novo
    
        station.setSlotsInUse(station.getSlotsInUse() + 1);
        stationRepository.save(station);
    
        user.setBalance(user.getBalance() - cost);
        userRepository.save(user);
    
        return bookChargeRepository.save(booking);
    }
    
    

    public List<BookCharge> getBookingsByUser(Long userId) {
        return bookChargeRepository.findByUserId(userId);
    }

    public BookCharge updateStatus(Long bookingId, String newStatus) {
        BookCharge booking = bookChargeRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        BookingStatus statusEnum = BookingStatus.valueOf(newStatus.toUpperCase());
        booking.setStatus(statusEnum);

        // Se for concluído ou cancelado, liberar slot
        if (statusEnum == BookingStatus.COMPLETED || statusEnum == BookingStatus.CANCELLED) {
            ChargerStation station = booking.getChargerStation();
            int inUse = station.getSlotsInUse();
            station.setSlotsInUse(Math.max(0, inUse - 1)); // nunca fica negativo
            stationRepository.save(station);
        }

        return bookChargeRepository.save(booking);
    }
}
