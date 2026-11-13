package prova.appnativo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoFisicaResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        Long professorId,
        String professorNome,
        LocalDate dataAvaliacao,
        BigDecimal peso,
        BigDecimal altura,
        BigDecimal imc,
        String classificacaoImc,
        BigDecimal percentualGordura,
        BigDecimal massaGorda,
        BigDecimal massaMagra,
        BigDecimal massaMuscular,
        BigDecimal torax,
        BigDecimal cintura,
        BigDecimal quadril,
        BigDecimal relacaoCinturaQuadril,
        BigDecimal braco,
        BigDecimal coxa,
        Integer frequenciaCardiacaRepouso,
        String observacoes
) {
}
