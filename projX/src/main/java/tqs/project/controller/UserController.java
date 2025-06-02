package tqs.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.dto.UserDTO;
import tqs.project.model.User;
import tqs.project.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}/addFunds")
    public ResponseEntity<?> addFunds(@PathVariable Long id, @RequestParam double amount) {
    try {
        User updatedUser = userService.addFunds(id, amount);
        return ResponseEntity.ok(updatedUser);
    } catch (RuntimeException e) {
        return ResponseEntity
            .badRequest()
            .body("Erro ao adicionar fundos: " + e.getMessage());
    }
}
    @GetMapping("/{id}/balance")
    public ResponseEntity<Double> getUserBalance(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserBalance(id));
    }

}
