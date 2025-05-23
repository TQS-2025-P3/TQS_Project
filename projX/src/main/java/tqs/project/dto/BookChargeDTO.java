package tqs.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookChargeDTO {

    @NotNull(message = "O tempo é obrigatório.")
    private LocalDateTime time;

    @Min(value = 1, message = "A duração deve ser maior que 0.")
    private int duration;

    @NotNull(message = "O estado é obrigatório.")
    private String status;

    @NotNull(message = "O ID do utilizador é obrigatório.")
    private Long userId;

    @NotNull(message = "O ID do carro é obrigatório.")
    private Long carId;

    @NotNull(message = "O ID da estação é obrigatório.")
    private Long chargerStationId;

    public BookChargeDTO() {}

    public BookChargeDTO(LocalDateTime time, int duration, String status,
                         Long userId, Long carId, Long chargerStationId) {
        this.time = time;
        this.duration = duration;
        this.status = status;
        this.userId = userId;
        this.carId = carId;
        this.chargerStationId = chargerStationId;
    }
}
