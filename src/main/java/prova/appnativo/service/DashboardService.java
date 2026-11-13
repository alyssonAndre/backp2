package prova.appnativo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Mensalidade;
import prova.appnativo.domain.enums.StatusAluno;
import prova.appnativo.domain.enums.StatusMensalidade;
import prova.appnativo.dto.DashboardResponse;
import prova.appnativo.dto.MensalidadeResponse;
import prova.appnativo.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardService {

    private static final int LIMITE_LISTAS = 5;

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final PlanoRepository planoRepository;
    private final TreinoRepository treinoRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final MensalidadeRepository mensalidadeRepository;
    private final MensalidadeService mensalidadeService;

    public DashboardService(
            AlunoRepository alunoRepository,
            ProfessorRepository professorRepository,
            PlanoRepository planoRepository,
            TreinoRepository treinoRepository,
            AvaliacaoFisicaRepository avaliacaoFisicaRepository,
            MensalidadeRepository mensalidadeRepository,
            MensalidadeService mensalidadeService
    ) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.planoRepository = planoRepository;
        this.treinoRepository = treinoRepository;
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
        this.mensalidadeRepository = mensalidadeRepository;
        this.mensalidadeService = mensalidadeService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse resumo() {
        LocalDate hoje = LocalDate.now();
        YearMonth mesAtual = YearMonth.from(hoje);

        List<Mensalidade> mensalidades = mensalidadeRepository.findAll();

        List<Mensalidade> atrasadas = mensalidades.stream()
                .filter(m -> m.getSituacao() == StatusMensalidade.ATRASADA)
                .sorted(Comparator.comparing(Mensalidade::getDataVencimento))
                .toList();

        long pendentes = mensalidades.stream()
                .filter(m -> m.getSituacao() == StatusMensalidade.PENDENTE)
                .count();

        BigDecimal recebidaNoMes = somar(mensalidades.stream()
                .filter(m -> m.getStatus() == StatusMensalidade.PAGA)
                .filter(m -> m.getDataPagamento() != null && YearMonth.from(m.getDataPagamento()).equals(mesAtual))
                .toList());

        BigDecimal previstaNoMes = somar(mensalidades.stream()
                .filter(m -> m.getStatus() != StatusMensalidade.CANCELADA)
                .filter(m -> YearMonth.from(m.getDataVencimento()).equals(mesAtual))
                .toList());

        long inadimplentes = atrasadas.stream()
                .map(m -> m.getAluno().getId())
                .distinct()
                .count();

        List<MensalidadeResponse> proximosVencimentos = mensalidades.stream()
                .filter(m -> m.getSituacao() == StatusMensalidade.PENDENTE)
                .filter(m -> !m.getDataVencimento().isAfter(hoje.plusDays(15)))
                .sorted(Comparator.comparing(Mensalidade::getDataVencimento))
                .limit(LIMITE_LISTAS)
                .map(mensalidadeService::toResponse)
                .toList();

        return new DashboardResponse(
                alunoRepository.count(),
                alunoRepository.countByStatus(StatusAluno.ATIVO),
                inadimplentes,
                professorRepository.countByAtivoTrue(),
                planoRepository.countByAtivoTrue(),
                treinoRepository.countByAtivoTrue(),
                avaliacaoFisicaRepository.countByDataAvaliacaoBetween(mesAtual.atDay(1), mesAtual.atEndOfMonth()),
                pendentes,
                atrasadas.size(),
                recebidaNoMes,
                previstaNoMes,
                somar(atrasadas),
                atrasadas.stream().limit(LIMITE_LISTAS).map(mensalidadeService::toResponse).toList(),
                proximosVencimentos
        );
    }

    private BigDecimal somar(List<Mensalidade> mensalidades) {
        return mensalidades.stream()
                .map(Mensalidade::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
