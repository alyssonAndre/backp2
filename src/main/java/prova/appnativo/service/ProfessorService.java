package prova.appnativo.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Professor;
import prova.appnativo.dto.ProfessorRequest;
import prova.appnativo.dto.ProfessorResponse;
import prova.appnativo.exception.ProfessorException;
import prova.appnativo.repository.AvaliacaoFisicaRepository;
import prova.appnativo.repository.ProfessorRepository;
import prova.appnativo.repository.TreinoRepository;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final TreinoRepository treinoRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;

    public ProfessorService(
            ProfessorRepository professorRepository,
            TreinoRepository treinoRepository,
            AvaliacaoFisicaRepository avaliacaoFisicaRepository
    ) {
        this.professorRepository = professorRepository;
        this.treinoRepository = treinoRepository;
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
    }

    @Transactional
    public ProfessorResponse create(ProfessorRequest request) {
        String cpf = TextoUtils.somenteDigitos(request.cpf());
        String email = request.email().trim().toLowerCase();
        String cref = request.cref().trim().toUpperCase();

        if (professorRepository.existsByCpf(cpf)) {
            throw new ProfessorException.DuplicateCpf();
        }
        if (professorRepository.existsByEmailIgnoreCase(email)) {
            throw new ProfessorException.DuplicateEmail();
        }
        if (professorRepository.existsByCrefIgnoreCase(cref)) {
            throw new ProfessorException.DuplicateCref();
        }

        Professor professor = new Professor();
        preencher(professor, request, cpf, email, cref);
        professor.setAtivo(request.ativo() == null || request.ativo());

        return toResponse(professorRepository.save(professor));
    }

    @Transactional(readOnly = true)
    public List<ProfessorResponse> findAll(String busca, Boolean ativo) {
        String termo = TextoUtils.limpar(busca);
        String termoDigitos = termo == null ? "" : TextoUtils.somenteDigitos(termo);

        return professorRepository.findAll(Sort.by("nome"))
                .stream()
                .filter(professor -> ativo == null || professor.getAtivo().equals(ativo))
                .filter(professor -> termo == null
                        || TextoUtils.contemIgnorandoCaixa(professor.getNome(), termo)
                        || TextoUtils.contemIgnorandoCaixa(professor.getEspecialidade(), termo)
                        || TextoUtils.contemIgnorandoCaixa(professor.getCref(), termo)
                        || (!termoDigitos.isEmpty() && professor.getCpf().contains(termoDigitos)))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfessorResponse findById(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProfessorResponse update(Long id, ProfessorRequest request) {
        Professor professor = buscarEntidade(id);

        String cpf = TextoUtils.somenteDigitos(request.cpf());
        String email = request.email().trim().toLowerCase();
        String cref = request.cref().trim().toUpperCase();

        if (professorRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new ProfessorException.DuplicateCpf();
        }
        if (professorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ProfessorException.DuplicateEmail();
        }
        if (professorRepository.existsByCrefIgnoreCaseAndIdNot(cref, id)) {
            throw new ProfessorException.DuplicateCref();
        }

        preencher(professor, request, cpf, email, cref);
        if (request.ativo() != null) {
            professor.setAtivo(request.ativo());
        }

        return toResponse(professorRepository.save(professor));
    }

    @Transactional
    public ProfessorResponse alternarStatus(Long id) {
        Professor professor = buscarEntidade(id);
        professor.setAtivo(!professor.getAtivo());
        return toResponse(professorRepository.save(professor));
    }

    @Transactional
    public void delete(Long id) {
        Professor professor = buscarEntidade(id);

        if (treinoRepository.existsByProfessorId(id) || avaliacaoFisicaRepository.existsByProfessorId(id)) {
            throw new ProfessorException.EmUso();
        }

        professorRepository.delete(professor);
    }

    public Professor buscarEntidade(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(ProfessorException.NotFound::new);
    }

    private void preencher(Professor professor, ProfessorRequest request, String cpf, String email, String cref) {
        professor.setNome(request.nome().trim());
        professor.setCpf(cpf);
        professor.setEmail(email);
        professor.setTelefone(TextoUtils.limpar(request.telefone()));
        professor.setCref(cref);
        professor.setEspecialidade(TextoUtils.limpar(request.especialidade()));
        professor.setDataAdmissao(request.dataAdmissao());
    }

    private ProfessorResponse toResponse(Professor professor) {
        return new ProfessorResponse(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getEmail(),
                professor.getTelefone(),
                professor.getCref(),
                professor.getEspecialidade(),
                professor.getDataAdmissao(),
                professor.getAtivo(),
                treinoRepository.countByProfessorIdAndAtivoTrue(professor.getId()),
                avaliacaoFisicaRepository.countByProfessorId(professor.getId())
        );
    }
}
