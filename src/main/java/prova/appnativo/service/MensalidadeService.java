package prova.appnativo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Aluno;
import prova.appnativo.domain.Mensalidade;
import prova.appnativo.domain.enums.StatusMensalidade;
import prova.appnativo.dto.GerarMensalidadesRequest;
import prova.appnativo.dto.MensalidadeRequest;
import prova.appnativo.dto.MensalidadeResponse;
import prova.appnativo.dto.PagamentoRequest;
import prova.appnativo.exception.MensalidadeException;
import prova.appnativo.exception.PlanoException;
import prova.appnativo.repository.MensalidadeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MensalidadeService {

    private final MensalidadeRepository mensalidadeRepository;
    private final AlunoService alunoService;

    public MensalidadeService(MensalidadeRepository mensalidadeRepository, AlunoService alunoService) {
        this.mensalidadeRepository = mensalidadeRepository;
        this.alunoService = alunoService;
    }

    @Transactional
    public MensalidadeResponse create(MensalidadeRequest request) {
        Aluno aluno = alunoService.buscarEntidade(request.alunoId());

        validarVencimentoUnico(aluno.getId(), request.dataVencimento());

        Mensalidade mensalidade = new Mensalidade();
        mensalidade.setAluno(aluno);
        mensalidade.setValor(resolverValor(aluno, request.valor()));
        mensalidade.setDataVencimento(request.dataVencimento());
        mensalidade.setStatus(StatusMensalidade.PENDENTE);

        return toResponse(mensalidadeRepository.save(mensalidade));
    }

    @Transactional
    public List<MensalidadeResponse> gerar(GerarMensalidadesRequest request) {
        Aluno aluno = alunoService.buscarEntidade(request.alunoId());

        if (aluno.getPlano() == null) {
            throw new PlanoException.SemPlano();
        }

        int quantidade = request.quantidade() != null
                ? request.quantidade()
                : aluno.getPlano().getDuracaoMeses();

        List<Mensalidade> geradas = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            LocalDate vencimento = request.primeiroVencimento().plusMonths(i);

            if (mensalidadeRepository.existsByAlunoIdAndDataVencimentoAndStatusNot(
                    aluno.getId(), vencimento, StatusMensalidade.CANCELADA)) {
                continue;
            }

            Mensalidade mensalidade = new Mensalidade();
            mensalidade.setAluno(aluno);
            mensalidade.setValor(aluno.getPlano().getValorMensal());
            mensalidade.setDataVencimento(vencimento);
            mensalidade.setStatus(StatusMensalidade.PENDENTE);
            geradas.add(mensalidade);
        }

        return mensalidadeRepository.saveAll(geradas)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MensalidadeResponse> findAll(Long alunoId, StatusMensalidade status) {
        List<Mensalidade> mensalidades = alunoId != null
                ? mensalidadeRepository.findByAlunoId(alunoId)
                : mensalidadeRepository.findAllByOrderByDataVencimentoDesc();

        return mensalidades.stream()
                .filter(mensalidade -> status == null || mensalidade.getSituacao() == status)
                .sorted((a, b) -> b.getDataVencimento().compareTo(a.getDataVencimento()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MensalidadeResponse findById(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public MensalidadeResponse update(Long id, MensalidadeRequest request) {
        Mensalidade mensalidade = buscarEntidade(id);
        garantirEditavel(mensalidade);

        Aluno aluno = alunoService.buscarEntidade(request.alunoId());

        boolean mudouVencimentoOuAluno = !Objects.equals(mensalidade.getAluno().getId(), aluno.getId())
                || !mensalidade.getDataVencimento().equals(request.dataVencimento());
        if (mudouVencimentoOuAluno) {
            validarVencimentoUnico(aluno.getId(), request.dataVencimento());
        }

        mensalidade.setAluno(aluno);
        mensalidade.setValor(resolverValor(aluno, request.valor()));
        mensalidade.setDataVencimento(request.dataVencimento());

        return toResponse(mensalidadeRepository.save(mensalidade));
    }

    @Transactional
    public MensalidadeResponse registrarPagamento(Long id, PagamentoRequest request) {
        Mensalidade mensalidade = buscarEntidade(id);
        garantirEditavel(mensalidade);

        mensalidade.setStatus(StatusMensalidade.PAGA);
        mensalidade.setFormaPagamento(request.formaPagamento());
        mensalidade.setDataPagamento(request.dataPagamento() != null ? request.dataPagamento() : LocalDate.now());

        return toResponse(mensalidadeRepository.save(mensalidade));
    }

    @Transactional
    public MensalidadeResponse estornarPagamento(Long id) {
        Mensalidade mensalidade = buscarEntidade(id);

        if (mensalidade.getStatus() != StatusMensalidade.PAGA) {
            throw new MensalidadeException.NaoPaga();
        }

        mensalidade.setStatus(StatusMensalidade.PENDENTE);
        mensalidade.setFormaPagamento(null);
        mensalidade.setDataPagamento(null);

        return toResponse(mensalidadeRepository.save(mensalidade));
    }

    @Transactional
    public MensalidadeResponse cancelar(Long id) {
        Mensalidade mensalidade = buscarEntidade(id);
        garantirEditavel(mensalidade);

        mensalidade.setStatus(StatusMensalidade.CANCELADA);

        return toResponse(mensalidadeRepository.save(mensalidade));
    }

    @Transactional
    public void delete(Long id) {
        Mensalidade mensalidade = buscarEntidade(id);
        mensalidadeRepository.delete(mensalidade);
    }

    private Mensalidade buscarEntidade(Long id) {
        return mensalidadeRepository.findById(id)
                .orElseThrow(MensalidadeException.NotFound::new);
    }

    private void garantirEditavel(Mensalidade mensalidade) {
        if (mensalidade.getStatus() == StatusMensalidade.PAGA) {
            throw new MensalidadeException.JaPaga();
        }
        if (mensalidade.getStatus() == StatusMensalidade.CANCELADA) {
            throw new MensalidadeException.Cancelada();
        }
    }

    private void validarVencimentoUnico(Long alunoId, LocalDate vencimento) {
        if (mensalidadeRepository.existsByAlunoIdAndDataVencimentoAndStatusNot(
                alunoId, vencimento, StatusMensalidade.CANCELADA)) {
            throw new MensalidadeException.VencimentoDuplicado();
        }
    }

    private BigDecimal resolverValor(Aluno aluno, BigDecimal valorInformado) {
        if (valorInformado != null) {
            return valorInformado;
        }
        if (aluno.getPlano() == null) {
            throw new PlanoException.SemPlano();
        }
        return aluno.getPlano().getValorMensal();
    }

    MensalidadeResponse toResponse(Mensalidade mensalidade) {
        StatusMensalidade situacao = mensalidade.getSituacao();
        Aluno aluno = mensalidade.getAluno();

        long diasEmAtraso = situacao == StatusMensalidade.ATRASADA
                ? ChronoUnit.DAYS.between(mensalidade.getDataVencimento(), LocalDate.now())
                : 0;

        return new MensalidadeResponse(
                mensalidade.getId(),
                aluno.getId(),
                aluno.getNome(),
                aluno.getPlano() == null ? null : aluno.getPlano().getNome(),
                mensalidade.getValor(),
                mensalidade.getDataVencimento(),
                mensalidade.getDataPagamento(),
                mensalidade.getFormaPagamento(),
                situacao,
                diasEmAtraso
        );
    }
}
