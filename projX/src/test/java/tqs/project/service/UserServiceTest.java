package tqs.project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.dto.CarDTO;
import tqs.project.dto.UserDTO;
import tqs.project.model.Car;
import tqs.project.model.User;
import tqs.project.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testCreateUserWithCars() {
        
        CarDTO car1 = new CarDTO("Tesla", "Model Y", 400, null);
        CarDTO car2 = new CarDTO("Nissan", "Leaf", 270, null);


        UserDTO userDTO = new UserDTO();
        userDTO.setName("Joana");
        userDTO.setEmail("joana@example.com");
        userDTO.setPassword("admin123");
        userDTO.setCars(List.of(car1, car2));

        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Joana");
        savedUser.setEmail("joana@example.com");
        savedUser.setPassword("admin123");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        
        User result = userService.createUser(userDTO);

        
        assertNotNull(result);
        assertEquals("Joana", result.getName());
        assertEquals("joana@example.com", result.getEmail());
        assertEquals("admin123", result.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
