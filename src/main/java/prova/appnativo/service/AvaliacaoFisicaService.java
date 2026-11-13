package prova.appnativo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Aluno;
import prova.appnativo.domain.AvaliacaoFisica;
import prova.appnativo.domain.Professor;
import prova.appnativo.dto.AvaliacaoFisicaRequest;
import prova.appnativo.dto.AvaliacaoFisicaResponse;
import prova.appnativo.exception.AvaliacaoFisicaException;
import prova.appnativo.repository.AvaliacaoFisicaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class AvaliacaoFisicaService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final AlunoService alunoService;
    private final ProfessorService professorService;

    public AvaliacaoFisicaService(
            AvaliacaoFisicaRepository avaliacaoFisicaRepository,
            AlunoService alunoService,
            ProfessorService professorService
    ) {
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
        this.alunoService = alunoService;
        this.professorService = professorService;
    }

    @Transactional
    public AvaliacaoFisicaResponse create(AvaliacaoFisicaRequest request) {
        AvaliacaoFisica avaliacao = new AvaliacaoFisica();
        preencher(avaliacao, request);
        return toResponse(avaliacaoFisicaRepository.save(avaliacao));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoFisicaResponse> findAll(Long alunoId) {
        List<AvaliacaoFisica> avaliacoes = alunoId != null
                ? avaliacaoFisicaRepository.findByAlunoId(alunoId)
                : avaliacaoFisicaRepository.findAllByOrderByDataAvaliacaoDescIdDesc();

        return avaliacoes.stream()
                .sorted(Comparator.comparing(AvaliacaoFisica::getDataAvaliacao)
                        .thenComparing(AvaliacaoFisica::getId)
                        .reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvaliacaoFisicaResponse findById(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public AvaliacaoFisicaResponse update(Long id, AvaliacaoFisicaRequest request) {
        AvaliacaoFisica avaliacao = buscarEntidade(id);
        preencher(avaliacao, request);
        return toResponse(avaliacaoFisicaRepository.save(avaliacao));
    }

    @Transactional
    public void delete(Long id) {
        AvaliacaoFisica avaliacao = buscarEntidade(id);
        avaliacaoFisicaRepository.delete(avaliacao);
    }

    private AvaliacaoFisica buscarEntidade(Long id) {
        return avaliacaoFisicaRepository.findById(id)
                .orElseThrow(AvaliacaoFisicaException.NotFound::new);
    }

    private void preencher(AvaliacaoFisica avaliacao, AvaliacaoFisicaRequest request) {
        Aluno aluno = alunoService.buscarEntidade(request.alunoId());
        Professor professor = professorService.buscarEntidade(request.professorId());

        avaliacao.setAluno(aluno);
        avaliacao.setProfessor(professor);
        avaliacao.setDataAvaliacao(request.dataAvaliacao());
        avaliacao.setPeso(request.peso());
        avaliacao.setAltura(request.altura());
        avaliacao.setPercentualGordura(request.percentualGordura());
        avaliacao.setMassaMuscular(request.massaMuscular());
        avaliacao.setTorax(request.torax());
        avaliacao.setCintura(request.cintura());
        avaliacao.setQuadril(request.quadril());
        avaliacao.setBraco(request.braco());
        avaliacao.setCoxa(request.coxa());
        avaliacao.setFrequenciaCardiacaRepouso(request.frequenciaCardiacaRepouso());
        avaliacao.setObservacoes(TextoUtils.limpar(request.observacoes()));
    }

    static BigDecimal calcularImc(BigDecimal peso, BigDecimal altura) {
        return peso.divide(altura.multiply(altura), 2, RoundingMode.HALF_UP);
    }

    static String classificarImc(BigDecimal imc) {
        double valor = imc.doubleValue();
        if (valor < 18.5) return "Abaixo do peso";
        if (valor < 25) return "Peso normal";
        if (valor < 30) return "Sobrepeso";
        if (valor < 35) return "Obesidade grau I";
        if (valor < 40) return "Obesidade grau II";
        return "Obesidade grau III";
    }

    private AvaliacaoFisicaResponse toResponse(AvaliacaoFisica avaliacao) {
        BigDecimal imc = calcularImc(avaliacao.getPeso(), avaliacao.getAltura());

        BigDecimal massaGorda = null;
        BigDecimal massaMagra = null;
        if (avaliacao.getPercentualGordura() != null) {
            massaGorda = avaliacao.getPeso()
                    .multiply(avaliacao.getPercentualGordura())
                    .divide(CEM, 2, RoundingMode.HALF_UP);
            massaMagra = avaliacao.getPeso().subtract(massaGorda);
        }

        BigDecimal relacaoCinturaQuadril = null;
        if (avaliacao.getCintura() != null && avaliacao.getQuadril() != null) {
            relacaoCinturaQuadril = avaliacao.getCintura()
                    .divide(avaliacao.getQuadril(), 2, RoundingMode.HALF_UP);
        }

        return new AvaliacaoFisicaResponse(
                avaliacao.getId(),
                avaliacao.getAluno().getId(),
                avaliacao.getAluno().getNome(),
                avaliacao.getProfessor().getId(),
                avaliacao.getProfessor().getNome(),
                avaliacao.getDataAvaliacao(),
                avaliacao.getPeso(),
                avaliacao.getAltura(),
                imc,
                classificarImc(imc),
                avaliacao.getPercentualGordura(),
                massaGorda,
                massaMagra,
                avaliacao.getMassaMuscular(),
                avaliacao.getTorax(),
                avaliacao.getCintura(),
                avaliacao.getQuadril(),
                relacaoCinturaQuadril,
                avaliacao.getBraco(),
                avaliacao.getCoxa(),
                avaliacao.getFrequenciaCardiacaRepouso(),
                avaliacao.getObservacoes()
        );
    }
}
