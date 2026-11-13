package prova.appnativo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prova.appnativo.domain.Mensalidade;
import prova.appnativo.domain.enums.StatusMensalidade;

import java.time.LocalDate;
import java.util.List;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findByAlunoId(Long alunoId);

    List<Mensalidade> findAllByOrderByDataVencimentoDesc();

    boolean existsByAlunoIdAndDataVencimentoAndStatusNot(Long alunoId, LocalDate dataVencimento, StatusMensalidade status);

    long countByAlunoIdAndStatusAndDataVencimentoBefore(Long alunoId, StatusMensalidade status, LocalDate data);
}
