package prova.appnativo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prova.appnativo.domain.AvaliacaoFisica;

import java.time.LocalDate;
import java.util.List;

public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, Long> {

    List<AvaliacaoFisica> findByAlunoId(Long alunoId);

    List<AvaliacaoFisica> findAllByOrderByDataAvaliacaoDescIdDesc();

    boolean existsByProfessorId(Long professorId);

    long countByProfessorId(Long professorId);

    long countByDataAvaliacaoBetween(LocalDate inicio, LocalDate fim);
}
