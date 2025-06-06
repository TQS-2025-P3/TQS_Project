package tqs.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.MonthlyRevenueData;
import tqs.project.dto.RushHourData;
import tqs.project.dto.WeeklyTrendData;
import tqs.project.model.BookCharge;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.BookChargeRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private BookChargeRepository bookChargeRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private BookCharge booking1, booking2, booking3;

    @BeforeEach
    void setUp() {
        booking1 = createBooking(1L, LocalDateTime.now().withHour(8), BookingStatus.COMPLETED, 15.0);
        booking2 = createBooking(2L, LocalDateTime.now().withHour(18), BookingStatus.COMPLETED, 20.0);
        booking3 = createBooking(3L, LocalDateTime.now().withHour(8), BookingStatus.RESERVED, 25.0);
    }

    private BookCharge createBooking(Long id, LocalDateTime time, BookingStatus status, double cost) {
        BookCharge booking = new BookCharge();
        booking.setId(id);
        booking.setTime(time);
        booking.setStatus(status);
        booking.setCost(cost);
        return booking;
    }

    @Test
    @DisplayName("Deve retornar estatísticas de rush hour com dados reais")
    void shouldReturnRushHourStatisticsWithRealData() {
        List<BookCharge> bookings = Arrays.asList(booking1, booking2, booking3);
        when(bookChargeRepository.findByTimeAfter(any(LocalDateTime.class))).thenReturn(bookings);

        List<RushHourData> result = statisticsService.getRushHourStatistics();

        assertThat(result).hasSize(24);
        assertThat(result.get(8).getHour()).isEqualTo("08:00");
        assertThat(result.get(8).getReservations()).isEqualTo(2); // booking1 e booking3
        assertThat(result.get(18).getHour()).isEqualTo("18:00");
        assertThat(result.get(18).getReservations()).isEqualTo(1); // booking2
        verify(bookChargeRepository).findByTimeAfter(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar dados mock quando ocorre exceção nas estatísticas de rush hour")
    void shouldReturnMockDataWhenExceptionInRushHourStats() {
        when(bookChargeRepository.findByTimeAfter(any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        List<RushHourData> result = statisticsService.getRushHourStatistics();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getHour()).isEqualTo("06:00");
        verify(bookChargeRepository).findByTimeAfter(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar tendências semanais com dados reais")
    void shouldReturnWeeklyTrendsWithRealData() {
        List<BookCharge> dailyBookings = Arrays.asList(booking1, booking2, booking3);
        when(bookChargeRepository.findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(dailyBookings);

        List<WeeklyTrendData> result = statisticsService.getWeeklyTrends();

        assertThat(result).hasSize(7);
        assertThat(result.get(6).getReservations()).isEqualTo(3);
        assertThat(result.get(6).getCompleted()).isEqualTo(2); 
        verify(bookChargeRepository, times(7)).findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar dados mock quando ocorre exceção nas tendências semanais")
    void shouldReturnMockDataWhenExceptionInWeeklyTrends() {
        when(bookChargeRepository.findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        List<WeeklyTrendData> result = statisticsService.getWeeklyTrends();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(7);
        verify(bookChargeRepository).findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar receita mensal com dados reais")
    void shouldReturnMonthlyRevenueWithRealData() {
        when(bookChargeRepository.findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(booking1, booking2, booking3));

        List<MonthlyRevenueData> result = statisticsService.getMonthlyRevenue();

        assertThat(result).hasSize(6);
        verify(bookChargeRepository, times(6)).findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar dados mock quando ocorre exceção na receita mensal")
    void shouldReturnMockDataWhenExceptionInMonthlyRevenue() {
        when(bookChargeRepository.findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        List<MonthlyRevenueData> result = statisticsService.getMonthlyRevenue();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(6);
        verify(bookChargeRepository).findByTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar receita mensal por estação com dados reais")
    void shouldReturnMonthlyRevenueByStationWithRealData() {
        Long stationId = 1L;
        List<BookCharge> stationBookings = Arrays.asList(booking1);
        when(bookChargeRepository.findByChargerStationIdAndTimeBetween(eq(stationId), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(stationBookings);

        List<MonthlyRevenueData> result = statisticsService.getMonthlyRevenueByStation(stationId);

        assertThat(result).hasSize(6);
        verify(bookChargeRepository, times(6))
            .findByChargerStationIdAndTimeBetween(eq(stationId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar dados mock quando ocorre exceção na receita mensal por estação")
    void shouldReturnMockDataWhenExceptionInMonthlyRevenueByStation() {
        Long stationId = 1L;
        when(bookChargeRepository.findByChargerStationIdAndTimeBetween(eq(stationId), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        List<MonthlyRevenueData> result = statisticsService.getMonthlyRevenueByStation(stationId);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(6);
        verify(bookChargeRepository)
            .findByChargerStationIdAndTimeBetween(eq(stationId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar estatísticas de rush hour por estação")
    void shouldReturnRushHourStatisticsByStation() {
        Long stationId = 1L;
        List<BookCharge> stationBookings = Arrays.asList(booking1, booking3);
        when(bookChargeRepository.findByChargerStationIdAndTimeAfter(eq(stationId), any(LocalDateTime.class)))
            .thenReturn(stationBookings);

        List<RushHourData> result = statisticsService.getRushHourStatisticsByStation(stationId);

        assertThat(result).hasSize(24);
        assertThat(result.get(8).getReservations()).isEqualTo(2);
        verify(bookChargeRepository)
            .findByChargerStationIdAndTimeAfter(eq(stationId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar tendências semanais por estação")
    void shouldReturnWeeklyTrendsByStation() {
        Long stationId = 1L;
        List<BookCharge> stationBookings = Arrays.asList(booking1);
        when(bookChargeRepository.findByChargerStationIdAndTimeBetween(eq(stationId), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(stationBookings);

        List<WeeklyTrendData> result = statisticsService.getWeeklyTrendsByStation(stationId);

        assertThat(result).hasSize(7);
        verify(bookChargeRepository, times(7))
            .findByChargerStationIdAndTimeBetween(eq(stationId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há dados")
    void shouldReturnEmptyListWhenNoData() {
        when(bookChargeRepository.findByTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        List<RushHourData> result = statisticsService.getRushHourStatistics();

        assertThat(result).hasSize(24);
        assertThat(result.get(0).getReservations()).isEqualTo(0);
        verify(bookChargeRepository).findByTimeAfter(any(LocalDateTime.class));
    }
}