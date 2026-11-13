package prova.appnativo.dto;

import prova.appnativo.domain.enums.ObjetivoTreino;

import java.time.LocalDate;
import java.util.List;

public record TreinoResponse(
        Long id,
        String nome,
        Long alunoId,
        String alunoNome,
        Long professorId,
        String professorNome,
        ObjetivoTreino objetivo,
        LocalDate dataInicio,
        LocalDate dataFim,
        String observacoes,
        Boolean ativo,
        List<ExercicioResponse> exercicios
) {
}
