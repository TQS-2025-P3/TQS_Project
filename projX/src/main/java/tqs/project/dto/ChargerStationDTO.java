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

    @NotNull(message = "Preço por kWh é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço por kWh deve ser maior que zero.")
    private Double pricePerKwh;

    @NotNull(message = "O ID do staff responsável é obrigatório.")
    private Long staffId;

    public ChargerStationDTO() {}

    public ChargerStationDTO(String name, Double latitude, Double longitude, int slots, Double pricePerKwh, Long staffId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.slots = slots;
        this.pricePerKwh = pricePerKwh;
        this.staffId = staffId;
    }
}

