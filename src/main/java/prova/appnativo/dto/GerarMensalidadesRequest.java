package prova.appnativo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GerarMensalidadesRequest(

        @NotNull(message = "Aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "Data do primeiro vencimento é obrigatória")
        LocalDate primeiroVencimento,

        @Min(value = 1, message = "Gere ao menos 1 mensalidade")
        @Max(value = 24, message = "Gere no máximo 24 mensalidades por vez")
        Integer quantidade
) {
}
