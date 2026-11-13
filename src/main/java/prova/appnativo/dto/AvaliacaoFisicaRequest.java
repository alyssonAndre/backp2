package prova.appnativo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoFisicaRequest(

        @NotNull(message = "Aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "Professor avaliador é obrigatório")
        Long professorId,

        @NotNull(message = "Data da avaliação é obrigatória")
        @PastOrPresent(message = "Data da avaliação não pode estar no futuro")
        LocalDate dataAvaliacao,

        @NotNull(message = "Peso é obrigatório")
        @DecimalMin(value = "20", message = "Peso mínimo de 20 kg")
        @DecimalMax(value = "400", message = "Peso máximo de 400 kg")
        BigDecimal peso,

        @NotNull(message = "Altura é obrigatória")
        @DecimalMin(value = "0.50", message = "Altura mínima de 0,50 m")
        @DecimalMax(value = "2.50", message = "Altura máxima de 2,50 m")
        BigDecimal altura,

        @DecimalMin(value = "1", message = "Percentual de gordura mínimo de 1%")
        @DecimalMax(value = "70", message = "Percentual de gordura máximo de 70%")
        BigDecimal percentualGordura,

        @DecimalMin(value = "1", message = "Massa muscular inválida")
        BigDecimal massaMuscular,

        @DecimalMin(value = "1", message = "Medida inválida")
        BigDecimal torax,

        @DecimalMin(value = "1", message = "Medida inválida")
        BigDecimal cintura,

        @DecimalMin(value = "1", message = "Medida inválida")
        BigDecimal quadril,

        @DecimalMin(value = "1", message = "Medida inválida")
        BigDecimal braco,

        @DecimalMin(value = "1", message = "Medida inválida")
        BigDecimal coxa,

        @Min(value = 30, message = "Frequência cardíaca mínima de 30 bpm")
        @Max(value = 220, message = "Frequência cardíaca máxima de 220 bpm")
        Integer frequenciaCardiacaRepouso,

        @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
        String observacoes
) {
}
