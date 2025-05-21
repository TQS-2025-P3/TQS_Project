package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.model.ChargerStation;
import tqs.project.repository.ChargerStationRepository;

import java.util.List;

@Service
public class ChargerStationService {

    @Autowired
    private ChargerStationRepository stationRepository;

    public List<ChargerStation> getAllStations() {
        return stationRepository.findAll();
    }

    public ChargerStation createStation(ChargerStation station) {
        return stationRepository.save(station);
    }
}
