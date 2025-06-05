package tqs.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.ChargerStationDTO;
import tqs.project.model.ChargerStation;
import tqs.project.service.ChargerStationService;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "http://localhost:3000")
public class ChargerStationController {

    @Autowired
    private ChargerStationService stationService;

    @GetMapping
    public List<ChargerStation> getAllStations() {
        return stationService.getAllStations();
    }

    @PostMapping
    public ChargerStation createStation(@Valid @RequestBody ChargerStationDTO stationDTO) {
        return stationService.createStation(stationDTO);
    }

    @PutMapping("/{id}")
    public ChargerStation updateStation(@PathVariable Long id, @Valid @RequestBody ChargerStationDTO stationDTO) {
        return stationService.updateStation(id, stationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        try {
            stationService.deleteStation(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ChargerStation getStationById(@PathVariable Long id) {
        return stationService.getStationById(id);
    }
    }