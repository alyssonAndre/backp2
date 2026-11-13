package prova.appnativo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prova.appnativo.domain.Plano;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    long countByAtivoTrue();
}
