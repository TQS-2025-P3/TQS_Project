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

    @GetMapping
    public List<BookCharge> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping
    public BookCharge createBooking(@Valid @RequestBody BookChargeDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }
}
