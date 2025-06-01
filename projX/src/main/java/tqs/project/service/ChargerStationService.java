package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.dto.ChargerStationDTO;
import tqs.project.model.ChargerStation;
import tqs.project.model.Staff;
import tqs.project.repository.ChargerStationRepository;
import tqs.project.repository.StaffRepository;

import java.util.List;

@Service
public class ChargerStationService {

    @Autowired
    private ChargerStationRepository stationRepository;

    @Autowired
    private StaffRepository staffRepository;

    public List<ChargerStation> getAllStations() {
        return stationRepository.findAll();
    }

    public ChargerStation createStation(ChargerStationDTO dto) {
        Staff staff = staffRepository.findById(dto.getStaffId()).orElseThrow(() ->
            new IllegalArgumentException("Staff com ID " + dto.getStaffId() + " não existe.")
        );

        ChargerStation station = new ChargerStation();
        station.setName(dto.getName());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setSlots(dto.getSlots());
        station.setStaff(staff);

        return stationRepository.save(station);
    }
}
