package prova.appnativo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PlanoRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "Valor mensal é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor mensal deve ser maior que zero")
        BigDecimal valorMensal,

        @NotNull(message = "Duração é obrigatória")
        @Min(value = 1, message = "Duração mínima é de 1 mês")
        @Max(value = 36, message = "Duração máxima é de 36 meses")
        Integer duracaoMeses,

        Boolean ativo
) {
}
