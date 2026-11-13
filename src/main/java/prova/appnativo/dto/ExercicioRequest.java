package prova.appnativo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ExercicioRequest(

        @NotBlank(message = "Nome do exercício é obrigatório")
        String nome,

        String grupoMuscular,

        @NotNull(message = "Número de séries é obrigatório")
        @Min(value = 1, message = "Mínimo de 1 série")
        @Max(value = 20, message = "Máximo de 20 séries")
        Integer series,

        @NotBlank(message = "Repetições são obrigatórias")
        String repeticoes,

        @DecimalMin(value = "0", message = "Carga não pode ser negativa")
        BigDecimal carga,

        @Min(value = 0, message = "Descanso não pode ser negativo")
        Integer descansoSegundos,

        String observacao
) {
}
