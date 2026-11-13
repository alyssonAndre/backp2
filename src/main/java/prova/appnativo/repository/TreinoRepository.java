package prova.appnativo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prova.appnativo.domain.Treino;

import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByAlunoId(Long alunoId);

    List<Treino> findAllByOrderByDataInicioDesc();

    boolean existsByProfessorId(Long professorId);

    long countByProfessorIdAndAtivoTrue(Long professorId);

    long countByAtivoTrue();
}
