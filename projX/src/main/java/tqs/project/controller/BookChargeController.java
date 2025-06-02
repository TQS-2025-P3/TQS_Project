package tqs.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.BookChargeDTO;
import tqs.project.model.BookCharge;
import tqs.project.service.BookChargeService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class BookChargeController {

    @Autowired
    private BookChargeService bookingService;

    // Criar nova reserva
    @PostMapping
    public BookCharge createBooking(@Valid @RequestBody BookChargeDTO dto) {
        return bookingService.createBooking(dto);
    }

    // Obter todas as reservas de um utilizador
    @GetMapping("/user/{userId}")
    public List<BookCharge> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUser(userId);
    }

    // Atualizar status de uma reserva (ex: COMPLETED ou CANCELLED)
    @PatchMapping("/{id}/status")
    public BookCharge updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        return bookingService.updateStatus(id, status);
    }

    
}
