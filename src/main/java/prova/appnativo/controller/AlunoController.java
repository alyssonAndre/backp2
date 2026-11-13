package prova.appnativo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import prova.appnativo.domain.enums.SituacaoFinanceira;
import prova.appnativo.domain.enums.StatusAluno;
import prova.appnativo.dto.AlunoRequest;
import prova.appnativo.dto.AlunoResponse;
import prova.appnativo.service.AlunoService;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoResponse create(@Valid @RequestBody AlunoRequest request) {
        return alunoService.create(request);
    }

    @GetMapping
    public List<AlunoResponse> findAll(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) StatusAluno status,
            @RequestParam(required = false) Long planoId,
            @RequestParam(required = false) SituacaoFinanceira situacao
    ) {
        return alunoService.findAll(busca, status, planoId, situacao);
    }

    @GetMapping("/{id}")
    public AlunoResponse findById(@PathVariable Long id) {
        return alunoService.findById(id);
    }

    @PutMapping("/{id}")
    public AlunoResponse update(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {
        return alunoService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public AlunoResponse alternarStatus(@PathVariable Long id) {
        return alunoService.alternarStatus(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        alunoService.delete(id);
    }
}
