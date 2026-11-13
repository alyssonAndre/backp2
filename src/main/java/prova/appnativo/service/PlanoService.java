package prova.appnativo.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prova.appnativo.domain.Plano;
import prova.appnativo.dto.PlanoRequest;
import prova.appnativo.dto.PlanoResponse;
import prova.appnativo.exception.PlanoException;
import prova.appnativo.repository.AlunoRepository;
import prova.appnativo.repository.PlanoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;
    private final AlunoRepository alunoRepository;

    public PlanoService(PlanoRepository planoRepository, AlunoRepository alunoRepository) {
        this.planoRepository = planoRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public PlanoResponse create(PlanoRequest request) {
        String nome = request.nome().trim();

        if (planoRepository.existsByNomeIgnoreCase(nome)) {
            throw new PlanoException.DuplicateNome();
        }

        Plano plano = new Plano();
        preencher(plano, request);
        plano.setAtivo(request.ativo() == null || request.ativo());

        return toResponse(planoRepository.save(plano));
    }

    @Transactional(readOnly = true)
    public List<PlanoResponse> findAll(Boolean ativo) {
        return planoRepository.findAll(Sort.by("valorMensal").descending())
                .stream()
                .filter(plano -> ativo == null || plano.getAtivo().equals(ativo))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlanoResponse findById(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public PlanoResponse update(Long id, PlanoRequest request) {
        Plano plano = buscarEntidade(id);

        if (planoRepository.existsByNomeIgnoreCaseAndIdNot(request.nome().trim(), id)) {
            throw new PlanoException.DuplicateNome();
        }

        preencher(plano, request);
        if (request.ativo() != null) {
            plano.setAtivo(request.ativo());
        }

        return toResponse(planoRepository.save(plano));
    }

    @Transactional
    public PlanoResponse alternarStatus(Long id) {
        Plano plano = buscarEntidade(id);
        plano.setAtivo(!plano.getAtivo());
        return toResponse(planoRepository.save(plano));
    }

    @Transactional
    public void delete(Long id) {
        Plano plano = buscarEntidade(id);

        long alunosVinculados = alunoRepository.countByPlanoId(id);
        if (alunosVinculados > 0) {
            throw new PlanoException.EmUso(alunosVinculados);
        }

        planoRepository.delete(plano);
    }

    public Plano buscarEntidade(Long id) {
        return planoRepository.findById(id)
                .orElseThrow(PlanoException.NotFound::new);
    }

    private void preencher(Plano plano, PlanoRequest request) {
        plano.setNome(request.nome().trim());
        plano.setDescricao(TextoUtils.limpar(request.descricao()));
        plano.setValorMensal(request.valorMensal());
        plano.setDuracaoMeses(request.duracaoMeses());
    }

    private PlanoResponse toResponse(Plano plano) {
        return new PlanoResponse(
                plano.getId(),
                plano.getNome(),
                plano.getDescricao(),
                plano.getValorMensal(),
                plano.getDuracaoMeses(),
                plano.getValorMensal().multiply(BigDecimal.valueOf(plano.getDuracaoMeses())),
                plano.getAtivo(),
                alunoRepository.countByPlanoId(plano.getId())
        );
    }
}
