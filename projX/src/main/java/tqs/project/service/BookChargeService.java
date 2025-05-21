package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.model.BookCharge;
import tqs.project.repository.BookChargeRepository;

import java.util.List;

@Service
public class BookChargeService {

    @Autowired
    private BookChargeRepository bookingRepository;

    public List<BookCharge> getAllBookings() {
        return bookingRepository.findAll();
    }

    public BookCharge createBooking(BookCharge booking) {
        return bookingRepository.save(booking);
    }
}
