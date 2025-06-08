package tqs.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.ChargerStationDTO;
import tqs.project.model.ChargerStation;
import tqs.project.service.ChargerStationService;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> createStation(@Valid @RequestBody ChargerStationDTO stationDTO) {
        try {
            ChargerStation station = stationService.createStation(stationDTO);
            return ResponseEntity.ok(station);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStation(@PathVariable Long id, @Valid @RequestBody ChargerStationDTO stationDTO) {
        try {
            ChargerStation station = stationService.updateStation(id, stationDTO);
            return ResponseEntity.ok(station);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        try {
            stationService.deleteStation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ChargerStation getStationById(@PathVariable Long id) {
        return stationService.getStationById(id);
    }
}