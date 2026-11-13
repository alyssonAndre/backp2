package prova.appnativo.dto;

import prova.appnativo.domain.enums.FormaPagamento;
import prova.appnativo.domain.enums.StatusMensalidade;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MensalidadeResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        String planoNome,
        BigDecimal valor,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        FormaPagamento formaPagamento,
        StatusMensalidade status,
        long diasEmAtraso
) {
}
