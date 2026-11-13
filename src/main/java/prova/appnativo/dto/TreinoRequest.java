package prova.appnativo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import prova.appnativo.domain.enums.ObjetivoTreino;

import java.time.LocalDate;
import java.util.List;

public record TreinoRequest(

        @NotBlank(message = "Nome da ficha é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String nome,

        @NotNull(message = "Aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "Professor é obrigatório")
        Long professorId,

        @NotNull(message = "Objetivo é obrigatório")
        ObjetivoTreino objetivo,

        @NotNull(message = "Data de início é obrigatória")
        LocalDate dataInicio,

        LocalDate dataFim,

        @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
        String observacoes,

        @NotEmpty(message = "Adicione ao menos um exercício")
        List<@Valid ExercicioRequest> exercicios
) {
}
