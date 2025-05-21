package tqs.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.project.model.BookCharge;
import tqs.project.service.BookChargeService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookChargeController {

    @Autowired
    private BookChargeService bookingService;

    @GetMapping
    public List<BookCharge> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping
    public BookCharge createBooking(@RequestBody BookCharge booking) {
        return bookingService.createBooking(booking);
    }
}
