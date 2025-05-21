package tqs.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.project.model.ChargerStation;
import tqs.project.service.ChargerStationService;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "http://localhost:5173")
public class ChargerStationController {

    @Autowired
    private ChargerStationService stationService;

    @GetMapping
    public List<ChargerStation> getAllStations() {
        return stationService.getAllStations();
    }

    @PostMapping
    public ChargerStation createStation(@RequestBody ChargerStation station) {
        return stationService.createStation(station);
    }
}
