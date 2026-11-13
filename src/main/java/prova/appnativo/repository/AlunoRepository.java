package prova.appnativo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prova.appnativo.domain.Aluno;
import prova.appnativo.domain.enums.StatusAluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByPlanoId(Long planoId);

    long countByStatus(StatusAluno status);
}
