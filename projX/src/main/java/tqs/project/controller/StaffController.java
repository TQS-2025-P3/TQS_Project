package tqs.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.StaffDTO;
import tqs.project.model.Staff;
import tqs.project.service.StaffService;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@CrossOrigin(origins = "http://localhost:5173")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping
    public Staff createStaff(@Valid @RequestBody StaffDTO staffDTO) {
        return staffService.createStaff(staffDTO);
    }

    @GetMapping
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{id}")
    public Staff getStaffById(@PathVariable Long id) {
        return staffService.getStaffById(id);
    }
}
