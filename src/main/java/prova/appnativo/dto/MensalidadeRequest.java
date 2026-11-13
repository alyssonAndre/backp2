package prova.appnativo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MensalidadeRequest(

        @NotNull(message = "Aluno é obrigatório")
        Long alunoId,

        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "Data de vencimento é obrigatória")
        LocalDate dataVencimento
) {
}
