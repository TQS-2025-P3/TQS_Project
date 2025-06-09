package tqs.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.dto.MonthlyRevenueData;
import tqs.project.dto.RushHourData;
import tqs.project.dto.WeeklyTrendData;
import tqs.project.service.StatisticsService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<RushHourData> rushHourData;
    private List<WeeklyTrendData> weeklyTrendData;
    private List<MonthlyRevenueData> monthlyRevenueData;

    @BeforeEach
    void setUp() {
        rushHourData = Arrays.asList(
            new RushHourData("08:00", 25),
            new RushHourData("18:00", 35)
        );

        weeklyTrendData = Arrays.asList(
            new WeeklyTrendData("01/06", 120, 115),
            new WeeklyTrendData("02/06", 135, 128)
        );

        monthlyRevenueData = Arrays.asList(
            new MonthlyRevenueData("Mai", 3500.0, 350),
            new MonthlyRevenueData("Jun", 3800.0, 380)
        );
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("GET /api/reservations/rush-hour-stats - Deve retornar estatísticas de rush hour")
    void shouldReturnRushHourStats() throws Exception {
        when(statisticsService.getRushHourStatistics()).thenReturn(rushHourData);

        mockMvc.perform(get("/api/reservations/rush-hour-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].hour").value("08:00"))
                .andExpect(jsonPath("$[0].reservations").value(25))
                .andExpect(jsonPath("$[1].hour").value("18:00"))
                .andExpect(jsonPath("$[1].reservations").value(35));

        verify(statisticsService).getRushHourStatistics();
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("GET /api/reservations/weekly-trends - Deve retornar tendências semanais")
    void shouldReturnWeeklyTrends() throws Exception {
        when(statisticsService.getWeeklyTrends()).thenReturn(weeklyTrendData);

        mockMvc.perform(get("/api/reservations/weekly-trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").value("01/06"))
                .andExpect(jsonPath("$[0].reservations").value(120))
                .andExpect(jsonPath("$[0].completed").value(115));

        verify(statisticsService).getWeeklyTrends();
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("GET /api/payments/monthly-revenue - Deve retornar receita mensal")
    void shouldReturnMonthlyRevenue() throws Exception {
        when(statisticsService.getMonthlyRevenue()).thenReturn(monthlyRevenueData);

        mockMvc.perform(get("/api/payments/monthly-revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].month").value("Mai"))
                .andExpect(jsonPath("$[0].revenue").value(3500.0))
                .andExpect(jsonPath("$[0].sessions").value(350));

        verify(statisticsService).getMonthlyRevenue();
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("GET /api/stations/{stationId}/monthly-revenue - Deve retornar receita mensal da estação")
    void shouldReturnStationMonthlyRevenue() throws Exception {
        when(statisticsService.getMonthlyRevenueByStation(1L)).thenReturn(monthlyRevenueData);

        mockMvc.perform(get("/api/stations/1/monthly-revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].month").value("Mai"))
                .andExpect(jsonPath("$[0].revenue").value(3500.0));

        verify(statisticsService).getMonthlyRevenueByStation(1L);
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("GET /api/stations/{stationId}/rush-hour-stats - Deve retornar rush hour da estação")
    void shouldReturnStationRushHourStats() throws Exception {
        when(statisticsService.getRushHourStatisticsByStation(1L)).thenReturn(rushHourData);

        mockMvc.perform(get("/api/stations/1/rush-hour-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].hour").value("08:00"))
                .andExpect(jsonPath("$[0].reservations").value(25));

        verify(statisticsService).getRushHourStatisticsByStation(1L);
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("GET /api/stations/{stationId}/weekly-trends - Deve retornar tendências semanais da estação")
    void shouldReturnStationWeeklyTrends() throws Exception {
        when(statisticsService.getWeeklyTrendsByStation(1L)).thenReturn(weeklyTrendData);

        mockMvc.perform(get("/api/stations/1/weekly-trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").value("01/06"))
                .andExpect(jsonPath("$[0].reservations").value(120));

        verify(statisticsService).getWeeklyTrendsByStation(1L);
    }

    @Requirement("TQSPROJECT-1220")
    @Test
    @DisplayName("POST /api/stations/monthly-revenue - Deve retornar receita de múltiplas estações")
    void shouldReturnMultipleStationsRevenue() throws Exception {
        List<Long> stationIds = Arrays.asList(1L, 2L);
        
        when(statisticsService.getMonthlyRevenueByStation(1L)).thenReturn(monthlyRevenueData);
        when(statisticsService.getMonthlyRevenueByStation(2L)).thenReturn(monthlyRevenueData);

        mockMvc.perform(post("/api/stations/monthly-revenue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").isArray())
                .andExpect(jsonPath("$[1]").isArray());

        verify(statisticsService).getMonthlyRevenueByStation(1L);
        verify(statisticsService).getMonthlyRevenueByStation(2L);
    }
}