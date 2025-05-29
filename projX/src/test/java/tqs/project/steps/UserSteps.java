package tqs.project.steps;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tqs.project.dto.CarDTO;
import tqs.project.dto.UserDTO;
import tqs.project.model.User;
import tqs.project.service.UserService;

public class UserSteps {
    
    private UserService userService;
    private UserDTO userDTO;
    private User createdUser;

    @Given("a user named {string} with email {string} and password {string}")
    public void aUserNamedWithEmailAndPassword(String name, String email, String password) {
        userDTO = new UserDTO();
        userDTO.setName(name);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setCars(new ArrayList<>());
    }

    @Given("the user owns the following cars:")
    public void theUserOwnsTheFollowingCars(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String brand = row.get("brand");
            String model = row.get("model");
            int rangeKm = Integer.parseInt(row.get("rangeKm"));
            Long ownerId = Long.parseLong(row.get("ownerId"));
    
            userDTO.getCars().add(new CarDTO(brand, model, rangeKm, ownerId));
        }
    }

    @When("the user is saved") 
        public void theUserIsSaved() {
        userService = mock(UserService.class);

        User saved = new User();
        saved.setId(1L);
        saved.setName(userDTO.getName());
        saved.setEmail(userDTO.getEmail());
        saved.setPassword(userDTO.getPassword());

        when(userService.createUser(any(UserDTO.class))).thenReturn(saved);

        createdUser = userService.createUser(userDTO);
    }

    @Then("the user should be created with name {string} and email {string}")
    public void theUserShouldBeCreatedWithNameAndEmail(String expectedName, String expectedEmail) {
        assertNotNull(createdUser);
        assertEquals(expectedName, createdUser.getName());
        assertEquals(expectedEmail, createdUser.getEmail());
    }
}
