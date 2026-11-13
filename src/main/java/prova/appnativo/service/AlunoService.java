package prova.appnativo.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Aluno;
import prova.appnativo.domain.Plano;
import prova.appnativo.domain.enums.SituacaoFinanceira;
import prova.appnativo.domain.enums.StatusAluno;
import prova.appnativo.domain.enums.StatusMensalidade;
import prova.appnativo.dto.AlunoRequest;
import prova.appnativo.dto.AlunoResponse;
import prova.appnativo.exception.AlunoException;
import prova.appnativo.exception.PlanoException;
import prova.appnativo.repository.AlunoRepository;
import prova.appnativo.repository.AvaliacaoFisicaRepository;
import prova.appnativo.repository.MensalidadeRepository;
import prova.appnativo.repository.TreinoRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PlanoService planoService;
    private final MensalidadeRepository mensalidadeRepository;
    private final TreinoRepository treinoRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;

    public AlunoService(
            AlunoRepository alunoRepository,
            PlanoService planoService,
            MensalidadeRepository mensalidadeRepository,
            TreinoRepository treinoRepository,
            AvaliacaoFisicaRepository avaliacaoFisicaRepository
    ) {
        this.alunoRepository = alunoRepository;
        this.planoService = planoService;
        this.mensalidadeRepository = mensalidadeRepository;
        this.treinoRepository = treinoRepository;
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
    }

    @Transactional
    public AlunoResponse create(AlunoRequest request) {
        String cpf = TextoUtils.somenteDigitos(request.cpf());
        String email = request.email().trim().toLowerCase();

        if (alunoRepository.existsByCpf(cpf)) {
            throw new AlunoException.DuplicateCpf();
        }
        if (alunoRepository.existsByEmailIgnoreCase(email)) {
            throw new AlunoException.DuplicateEmail();
        }

        Plano plano = planoService.buscarEntidade(request.planoId());
        if (!plano.getAtivo()) {
            throw new PlanoException.Inativo();
        }

        Aluno aluno = new Aluno();
        preencher(aluno, request, cpf, email);
        aluno.setPlano(plano);
        aluno.setDataMatricula(LocalDate.now());
        aluno.setStatus(request.status() == null ? StatusAluno.ATIVO : request.status());

        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> findAll(String busca, StatusAluno status, Long planoId, SituacaoFinanceira situacao) {
        String termo = TextoUtils.limpar(busca);
        String termoDigitos = termo == null ? "" : TextoUtils.somenteDigitos(termo);

        return alunoRepository.findAll(Sort.by("nome"))
                .stream()
                .filter(aluno -> status == null || aluno.getStatus() == status)
                .filter(aluno -> planoId == null || (aluno.getPlano() != null && aluno.getPlano().getId().equals(planoId)))
                .filter(aluno -> termo == null
                        || TextoUtils.contemIgnorandoCaixa(aluno.getNome(), termo)
                        || TextoUtils.contemIgnorandoCaixa(aluno.getEmail(), termo)
                        || (!termoDigitos.isEmpty() && aluno.getCpf().contains(termoDigitos)))
                .map(this::toResponse)
                .filter(aluno -> situacao == null || aluno.situacaoFinanceira() == situacao)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponse findById(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public AlunoResponse update(Long id, AlunoRequest request) {
        Aluno aluno = buscarEntidade(id);

        String cpf = TextoUtils.somenteDigitos(request.cpf());
        String email = request.email().trim().toLowerCase();

        if (alunoRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new AlunoException.DuplicateCpf();
        }
        if (alunoRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new AlunoException.DuplicateEmail();
        }

        Long planoAtualId = aluno.getPlano() == null ? null : aluno.getPlano().getId();
        if (!Objects.equals(planoAtualId, request.planoId())) {
            Plano novoPlano = planoService.buscarEntidade(request.planoId());
            if (!novoPlano.getAtivo()) {
                throw new PlanoException.Inativo();
            }
            aluno.setPlano(novoPlano);
        }

        preencher(aluno, request, cpf, email);
        if (request.status() != null) {
            aluno.setStatus(request.status());
        }

        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse alternarStatus(Long id) {
        Aluno aluno = buscarEntidade(id);
        aluno.setStatus(aluno.getStatus() == StatusAluno.ATIVO ? StatusAluno.INATIVO : StatusAluno.ATIVO);
        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public void delete(Long id) {
        Aluno aluno = buscarEntidade(id);

        mensalidadeRepository.deleteAll(mensalidadeRepository.findByAlunoId(id));
        treinoRepository.deleteAll(treinoRepository.findByAlunoId(id));
        avaliacaoFisicaRepository.deleteAll(avaliacaoFisicaRepository.findByAlunoId(id));

        alunoRepository.delete(aluno);
    }

    public Aluno buscarEntidade(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(AlunoException.NotFound::new);
    }

    private void preencher(Aluno aluno, AlunoRequest request, String cpf, String email) {
        aluno.setNome(request.nome().trim());
        aluno.setCpf(cpf);
        aluno.setEmail(email);
        aluno.setTelefone(TextoUtils.limpar(request.telefone()));
        aluno.setDataNascimento(request.dataNascimento());
    }

    private AlunoResponse toResponse(Aluno aluno) {
        long atrasadas = mensalidadeRepository.countByAlunoIdAndStatusAndDataVencimentoBefore(
                aluno.getId(),
                StatusMensalidade.PENDENTE,
                LocalDate.now()
        );

        Plano plano = aluno.getPlano();

        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getEmail(),
                aluno.getTelefone(),
                aluno.getDataNascimento(),
                Period.between(aluno.getDataNascimento(), LocalDate.now()).getYears(),
                aluno.getDataMatricula(),
                aluno.getStatus(),
                plano == null ? null : plano.getId(),
                plano == null ? null : plano.getNome(),
                plano == null ? null : plano.getValorMensal(),
                atrasadas > 0 ? SituacaoFinanceira.INADIMPLENTE : SituacaoFinanceira.EM_DIA,
                atrasadas
        );
    }
}
