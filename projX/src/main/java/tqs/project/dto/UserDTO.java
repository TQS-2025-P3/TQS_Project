package tqs.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class UserDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @Email(message = "O email deve ser válido.")
    @NotBlank(message = "O email é obrigatório.")
    private String email;

    @NotBlank(message = "A palavra-passe é obrigatória.")
    private String password;

    private List<CarDTO> cars;

    public UserDTO() {}

    public UserDTO(String name, String email, String password, List<CarDTO> cars) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cars = cars;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<CarDTO> getCars() { return cars; }
    public void setCars(List<CarDTO> cars) { this.cars = cars; }
}
