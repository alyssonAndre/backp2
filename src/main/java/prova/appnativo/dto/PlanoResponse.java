package prova.appnativo.dto;

import java.math.BigDecimal;

public record PlanoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal valorMensal,
        Integer duracaoMeses,
        BigDecimal valorTotal,
        Boolean ativo,
        long quantidadeAlunos
) {
}
