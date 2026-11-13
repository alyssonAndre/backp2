package prova.appnativo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import prova.appnativo.domain.enums.FormaPagamento;

import java.time.LocalDate;

public record PagamentoRequest(

        @NotNull(message = "Forma de pagamento é obrigatória")
        FormaPagamento formaPagamento,

        @PastOrPresent(message = "Data do pagamento não pode estar no futuro")
        LocalDate dataPagamento
) {
}
