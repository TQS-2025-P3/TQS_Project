package tqs.project.dto;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargerStationDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotNull(message = "Latitude é obrigatória.")
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória.")
    private Double longitude;

    @Min(value = 1, message = "Deve haver pelo menos 1 slot.")
    private int slots;

    @NotNull(message = "O ID do staff responsável é obrigatório.")
    private Long staffId;

    public ChargerStationDTO() {}

    public ChargerStationDTO(String name, Double latitude, Double longitude, int slots, Long staffId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.slots = slots;
        this.staffId = staffId;
    }
}
