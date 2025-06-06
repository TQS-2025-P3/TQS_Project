package tqs.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.dto.BookChargeDTO;
import tqs.project.model.BookCharge;
import tqs.project.model.enums.BookingStatus;
import tqs.project.service.BookChargeService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookChargeController.class)
class BookChargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookChargeService bookChargeService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookCharge booking;
    private BookChargeDTO bookingDTO;

    @BeforeEach
    void setUp() {
        booking = new BookCharge();
        booking.setId(1L);
        booking.setDuration(60);
        booking.setTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.RESERVED);
        booking.setCost(18.75);

        bookingDTO = new BookChargeDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStationId(1L);
        bookingDTO.setDuration(60);
    }

    @Test
    @DisplayName("POST /api/bookings - Deve criar reserva com sucesso")
    void shouldCreateBookingSuccessfully() throws Exception {
        when(bookChargeService.createBooking(any(BookChargeDTO.class))).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration").value(60))
                .andExpect(jsonPath("$.cost").value(18.75))
                .andExpect(jsonPath("$.status").value("RESERVED"));

        verify(bookChargeService).createBooking(any(BookChargeDTO.class));
    }

    @Test
    @DisplayName("GET /api/bookings/user/{userId} - Deve retornar reservas do utilizador")
    void shouldReturnBookingsByUser() throws Exception {
        List<BookCharge> bookings = Arrays.asList(booking);
        when(bookChargeService.getBookingsByUser(1L)).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].duration").value(60))
                .andExpect(jsonPath("$[0].status").value("RESERVED"))
                .andExpect(jsonPath("$[0].cost").value(18.75));

        verify(bookChargeService).getBookingsByUser(1L);
    }

    @Test
    @DisplayName("PATCH /api/bookings/{id}/status - Deve atualizar status da reserva")
    void shouldUpdateBookingStatus() throws Exception {
        BookCharge updatedBooking = new BookCharge();
        updatedBooking.setId(1L);
        updatedBooking.setStatus(BookingStatus.COMPLETED);
        updatedBooking.setDuration(60);
        updatedBooking.setCost(18.75);
        
        when(bookChargeService.updateStatus(1L, "COMPLETED")).thenReturn(updatedBooking);

        mockMvc.perform(patch("/api/bookings/1/status")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.duration").value(60));

        verify(bookChargeService).updateStatus(1L, "COMPLETED");
    }
}