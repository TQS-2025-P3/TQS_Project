package tqs.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookChargeDTO {

    @NotNull(message = "O ID do utilizador é obrigatório.")
    private Long userId;

    @NotNull(message = "O ID do carro é obrigatório.")
    private Long carId;

    @NotNull(message = "O ID da estação é obrigatório.")
    private Long stationId;

    private int duration; // duração estimada em minutos
}
