package prova.appnativo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Aluno;
import prova.appnativo.domain.Exercicio;
import prova.appnativo.domain.Professor;
import prova.appnativo.domain.Treino;
import prova.appnativo.domain.enums.StatusAluno;
import prova.appnativo.dto.ExercicioRequest;
import prova.appnativo.dto.ExercicioResponse;
import prova.appnativo.dto.TreinoRequest;
import prova.appnativo.dto.TreinoResponse;
import prova.appnativo.exception.AlunoException;
import prova.appnativo.exception.ProfessorException;
import prova.appnativo.exception.TreinoException;
import prova.appnativo.repository.TreinoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final AlunoService alunoService;
    private final ProfessorService professorService;

    public TreinoService(
            TreinoRepository treinoRepository,
            AlunoService alunoService,
            ProfessorService professorService
    ) {
        this.treinoRepository = treinoRepository;
        this.alunoService = alunoService;
        this.professorService = professorService;
    }

    @Transactional
    public TreinoResponse create(TreinoRequest request) {
        validarPeriodo(request);

        Aluno aluno = alunoService.buscarEntidade(request.alunoId());
        if (aluno.getStatus() != StatusAluno.ATIVO) {
            throw new AlunoException.Inativo();
        }

        Professor professor = professorService.buscarEntidade(request.professorId());
        if (!professor.getAtivo()) {
            throw new ProfessorException.Inativo();
        }

        Treino treino = new Treino();
        treino.setAluno(aluno);
        treino.setProfessor(professor);
        treino.setAtivo(true);
        preencher(treino, request);

        return toResponse(treinoRepository.save(treino));
    }

    @Transactional(readOnly = true)
    public List<TreinoResponse> findAll(Long alunoId, Long professorId, Boolean ativo) {
        return treinoRepository.findAllByOrderByDataInicioDesc()
                .stream()
                .filter(treino -> alunoId == null || treino.getAluno().getId().equals(alunoId))
                .filter(treino -> professorId == null || treino.getProfessor().getId().equals(professorId))
                .filter(treino -> ativo == null || treino.getAtivo().equals(ativo))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TreinoResponse findById(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public TreinoResponse update(Long id, TreinoRequest request) {
        Treino treino = buscarEntidade(id);
        validarPeriodo(request);

        if (!Objects.equals(treino.getAluno().getId(), request.alunoId())) {
            Aluno aluno = alunoService.buscarEntidade(request.alunoId());
            if (aluno.getStatus() != StatusAluno.ATIVO) {
                throw new AlunoException.Inativo();
            }
            treino.setAluno(aluno);
        }

        if (!Objects.equals(treino.getProfessor().getId(), request.professorId())) {
            Professor professor = professorService.buscarEntidade(request.professorId());
            if (!professor.getAtivo()) {
                throw new ProfessorException.Inativo();
            }
            treino.setProfessor(professor);
        }

        preencher(treino, request);

        return toResponse(treinoRepository.save(treino));
    }

    @Transactional
    public TreinoResponse alternarStatus(Long id) {
        Treino treino = buscarEntidade(id);
        treino.setAtivo(!treino.getAtivo());
        return toResponse(treinoRepository.save(treino));
    }

    @Transactional
    public void delete(Long id) {
        Treino treino = buscarEntidade(id);
        treinoRepository.delete(treino);
    }

    private Treino buscarEntidade(Long id) {
        return treinoRepository.findById(id)
                .orElseThrow(TreinoException.NotFound::new);
    }

    private void validarPeriodo(TreinoRequest request) {
        if (request.dataFim() != null && request.dataFim().isBefore(request.dataInicio())) {
            throw new TreinoException.PeriodoInvalido();
        }
    }

    private void preencher(Treino treino, TreinoRequest request) {
        treino.setNome(request.nome().trim());
        treino.setObjetivo(request.objetivo());
        treino.setDataInicio(request.dataInicio());
        treino.setDataFim(request.dataFim());
        treino.setObservacoes(TextoUtils.limpar(request.observacoes()));

        List<Exercicio> exercicios = new ArrayList<>();
        for (int i = 0; i < request.exercicios().size(); i++) {
            exercicios.add(toExercicio(request.exercicios().get(i), i + 1));
        }
        treino.substituirExercicios(exercicios);
    }

    private Exercicio toExercicio(ExercicioRequest request, int ordem) {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(request.nome().trim());
        exercicio.setGrupoMuscular(TextoUtils.limpar(request.grupoMuscular()));
        exercicio.setSeries(request.series());
        exercicio.setRepeticoes(request.repeticoes().trim());
        exercicio.setCarga(request.carga());
        exercicio.setDescansoSegundos(request.descansoSegundos());
        exercicio.setObservacao(TextoUtils.limpar(request.observacao()));
        exercicio.setOrdem(ordem);
        return exercicio;
    }

    private TreinoResponse toResponse(Treino treino) {
        List<ExercicioResponse> exercicios = treino.getExercicios()
                .stream()
                .map(exercicio -> new ExercicioResponse(
                        exercicio.getId(),
                        exercicio.getOrdem(),
                        exercicio.getNome(),
                        exercicio.getGrupoMuscular(),
                        exercicio.getSeries(),
                        exercicio.getRepeticoes(),
                        exercicio.getCarga(),
                        exercicio.getDescansoSegundos(),
                        exercicio.getObservacao()
                ))
                .toList();

        return new TreinoResponse(
                treino.getId(),
                treino.getNome(),
                treino.getAluno().getId(),
                treino.getAluno().getNome(),
                treino.getProfessor().getId(),
                treino.getProfessor().getNome(),
                treino.getObjetivo(),
                treino.getDataInicio(),
                treino.getDataFim(),
                treino.getObservacoes(),
                treino.getAtivo(),
                exercicios
        );
    }
}
