package prova.appnativo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import prova.appnativo.dto.TreinoRequest;
import prova.appnativo.dto.TreinoResponse;
import prova.appnativo.service.TreinoService;

import java.util.List;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoResponse create(@Valid @RequestBody TreinoRequest request) {
        return treinoService.create(request);
    }

    @GetMapping
    public List<TreinoResponse> findAll(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long professorId,
            @RequestParam(required = false) Boolean ativo
    ) {
        return treinoService.findAll(alunoId, professorId, ativo);
    }

    @GetMapping("/{id}")
    public TreinoResponse findById(@PathVariable Long id) {
        return treinoService.findById(id);
    }

    @PutMapping("/{id}")
    public TreinoResponse update(@PathVariable Long id, @Valid @RequestBody TreinoRequest request) {
        return treinoService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TreinoResponse alternarStatus(@PathVariable Long id) {
        return treinoService.alternarStatus(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        treinoService.delete(id);
    }
}
