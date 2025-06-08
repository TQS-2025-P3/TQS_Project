package tqs.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.project.dto.ChargerStationDTO;
import tqs.project.model.BookCharge;
import tqs.project.model.ChargerStation;
import tqs.project.model.Staff;
import tqs.project.model.enums.BookingStatus;
import tqs.project.repository.BookChargeRepository;
import tqs.project.repository.ChargerStationRepository;
import tqs.project.repository.StaffRepository;

import java.util.List;

@Service
public class ChargerStationService {

    @Autowired
    private ChargerStationRepository stationRepository;

    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private BookChargeRepository bookChargeRepository;

    private boolean isDuplicateName(String name, Long excludeStationId) {
        List<ChargerStation> existingStations = stationRepository.findByNameIgnoreCase(name);
        
        if (excludeStationId != null) {
            existingStations.removeIf(station -> station.getId().equals(excludeStationId));
        }
        
        return !existingStations.isEmpty();
    }

    private boolean isDuplicateCoordinates(double latitude, double longitude, Long excludeStationId) {
        ChargerStation existing = stationRepository.findByLatitudeAndLongitude(latitude, longitude);
        
        if (existing == null) {
            return false;
        }
        
        if (excludeStationId != null && existing.getId().equals(excludeStationId)) {
            return false;
        }
        
        return true;
    }

    public List<ChargerStation> getAllStations() {
        return stationRepository.findAll();
    }

    public ChargerStation createStation(ChargerStationDTO dto) {
        if (isDuplicateName(dto.getName(), null)) {
            throw new RuntimeException("Já existe uma estação com o nome: " + dto.getName() + 
                                     " (verificação ignora maiúsculas/minúsculas)");
        }
        
        if (isDuplicateCoordinates(dto.getLatitude(), dto.getLongitude(), null)) {
            throw new RuntimeException("Já existe uma estação nas coordenadas: " + 
                                     dto.getLatitude() + ", " + dto.getLongitude());
        }
        
        Staff staff = staffRepository.findById(dto.getStaffId()).orElseThrow(() ->
            new IllegalArgumentException("Staff com ID " + dto.getStaffId() + " não existe.")
        );

        ChargerStation station = new ChargerStation();
        station.setName(dto.getName());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setSlots(dto.getSlots());
        station.setSlotsInUse(0);
        station.setPricePerKwh(dto.getPricePerKwh());
        station.setStaff(staff);

        return stationRepository.save(station);
    }

    public ChargerStation updateStation(Long id, ChargerStationDTO dto) {
        ChargerStation existingStation = stationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Estação com ID " + id + " não encontrada."));

        if (isDuplicateName(dto.getName(), id)) {
            throw new RuntimeException("Já existe outra estação com o nome: " + dto.getName() + 
                                     " (verificação ignora maiúsculas/minúsculas)");
        }
        
        if (isDuplicateCoordinates(dto.getLatitude(), dto.getLongitude(), id)) {
            throw new RuntimeException("Já existe outra estação nas coordenadas: " + 
                                     dto.getLatitude() + ", " + dto.getLongitude());
        }

        existingStation.setName(dto.getName());
        existingStation.setLatitude(dto.getLatitude());
        existingStation.setLongitude(dto.getLongitude());
        existingStation.setSlots(dto.getSlots());
        existingStation.setPricePerKwh(dto.getPricePerKwh());

        Staff staff = staffRepository.findById(dto.getStaffId())
            .orElseThrow(() -> new IllegalArgumentException("Staff com ID " + dto.getStaffId() + " não existe."));
        existingStation.setStaff(staff);

        return stationRepository.save(existingStation);
    }

    @Transactional
    public void deleteStation(Long id) {
        if (!stationRepository.existsById(id)) {
            throw new IllegalArgumentException("Estação com ID " + id + " não encontrada.");
        }
        
        List<BookCharge> activeBookings = bookChargeRepository.findByChargerStationIdAndStatus(id, BookingStatus.RESERVED);
        
        if (!activeBookings.isEmpty()) {
            throw new RuntimeException("Não é possível apagar a estação. Existem " + 
                                     activeBookings.size() + " reserva(s) ativa(s). " +
                                     "Complete ou cancele as reservas primeiro.");
        }
        
        bookChargeRepository.deleteByChargerStationId(id);
        
        stationRepository.deleteById(id);
    }

    public ChargerStation getStationById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Estação com ID " + id + " não encontrada."));
    }
}