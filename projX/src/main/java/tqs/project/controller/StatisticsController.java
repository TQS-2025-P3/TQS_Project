package tqs.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.RushHourData;
import tqs.project.dto.WeeklyTrendData;
import tqs.project.dto.MonthlyRevenueData;
import tqs.project.service.StatisticsService;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://localhost:3306"})
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/reservations/rush-hour-stats")
    public List<RushHourData> getRushHourStats() {
        return statisticsService.getRushHourStatistics();
    }

    @GetMapping("/reservations/weekly-trends")
    public List<WeeklyTrendData> getWeeklyTrends() {
        return statisticsService.getWeeklyTrends();
    }

    @GetMapping("/payments/monthly-revenue")
    public List<MonthlyRevenueData> getMonthlyRevenue() {
        return statisticsService.getMonthlyRevenue();
    }

    @GetMapping("/stations/{stationId}/monthly-revenue")
    public List<MonthlyRevenueData> getStationMonthlyRevenue(@PathVariable Long stationId) {
        return statisticsService.getMonthlyRevenueByStation(stationId);
    }

    @GetMapping("/stations/{stationId}/rush-hour-stats")
    public List<RushHourData> getStationRushHourStats(@PathVariable Long stationId) {
        return statisticsService.getRushHourStatisticsByStation(stationId);
    }

    @GetMapping("/stations/{stationId}/weekly-trends")
    public List<WeeklyTrendData> getStationWeeklyTrends(@PathVariable Long stationId) {
        return statisticsService.getWeeklyTrendsByStation(stationId);
    }

    @PostMapping("/stations/monthly-revenue")
    public List<List<MonthlyRevenueData>> getMultipleStationsRevenue(@RequestBody List<Long> stationIds) {
        return stationIds.stream()
            .map(stationId -> statisticsService.getMonthlyRevenueByStation(stationId))
            .toList();
    }
}