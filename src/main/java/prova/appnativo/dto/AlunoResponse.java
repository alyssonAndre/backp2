package prova.appnativo.dto;

import prova.appnativo.domain.enums.SituacaoFinanceira;
import prova.appnativo.domain.enums.StatusAluno;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlunoResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        LocalDate dataNascimento,
        Integer idade,
        LocalDate dataMatricula,
        StatusAluno status,
        Long planoId,
        String planoNome,
        BigDecimal planoValorMensal,
        SituacaoFinanceira situacaoFinanceira,
        long mensalidadesAtrasadas
) {
}
