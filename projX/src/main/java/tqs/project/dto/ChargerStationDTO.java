package tqs.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargerStationDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "A localização é obrigatória.")
    private String location;

    @NotNull(message = "O ID do staff responsável é obrigatório.")
    private Long staffId;

    public ChargerStationDTO() {}

    public ChargerStationDTO(String name, String location, Long staffId) {
        this.name = name;
        this.location = location;
        this.staffId = staffId;
    }
}
