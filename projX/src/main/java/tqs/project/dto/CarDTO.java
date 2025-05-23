package tqs.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CarDTO {

    @NotBlank(message = "A marca é obrigatória.")
    private String brand;

    @NotBlank(message = "O modelo é obrigatório.")
    private String model;

    @NotNull(message = "A autonomia é obrigatória.")
    private Integer rangeKm;

    private Long ownerId;

    public CarDTO() {}

    public CarDTO(String brand, String model, Integer rangeKm, Long ownerId) {
        this.brand = brand;
        this.model = model;
        this.rangeKm = rangeKm;
        this.ownerId = ownerId;
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getRangeKm() { return rangeKm; }
    public void setRangeKm(Integer rangeKm) { this.rangeKm = rangeKm; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
