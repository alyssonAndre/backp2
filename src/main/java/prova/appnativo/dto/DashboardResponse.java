package prova.appnativo.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long totalAlunos,
        long alunosAtivos,
        long alunosInadimplentes,
        long professoresAtivos,
        long planosAtivos,
        long treinosAtivos,
        long avaliacoesNoMes,
        long mensalidadesPendentes,
        long mensalidadesAtrasadas,
        BigDecimal receitaRecebidaNoMes,
        BigDecimal receitaPrevistaNoMes,
        BigDecimal valorEmAtraso,
        List<MensalidadeResponse> mensalidadesEmAtraso,
        List<MensalidadeResponse> proximosVencimentos
) {
}
