package prova.appnativo.dto;

import java.time.LocalDate;

public record ProfessorResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        String cref,
        String especialidade,
        LocalDate dataAdmissao,
        Boolean ativo,
        long quantidadeTreinosAtivos,
        long quantidadeAvaliacoes
) {
}
