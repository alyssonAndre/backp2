package prova.appnativo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import prova.appnativo.dto.AvaliacaoFisicaRequest;
import prova.appnativo.dto.AvaliacaoFisicaResponse;
import prova.appnativo.service.AvaliacaoFisicaService;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoFisicaController {

    private final AvaliacaoFisicaService avaliacaoFisicaService;

    public AvaliacaoFisicaController(AvaliacaoFisicaService avaliacaoFisicaService) {
        this.avaliacaoFisicaService = avaliacaoFisicaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoFisicaResponse create(@Valid @RequestBody AvaliacaoFisicaRequest request) {
        return avaliacaoFisicaService.create(request);
    }

    @GetMapping
    public List<AvaliacaoFisicaResponse> findAll(@RequestParam(required = false) Long alunoId) {
        return avaliacaoFisicaService.findAll(alunoId);
    }

    @GetMapping("/{id}")
    public AvaliacaoFisicaResponse findById(@PathVariable Long id) {
        return avaliacaoFisicaService.findById(id);
    }

    @PutMapping("/{id}")
    public AvaliacaoFisicaResponse update(@PathVariable Long id, @Valid @RequestBody AvaliacaoFisicaRequest request) {
        return avaliacaoFisicaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        avaliacaoFisicaService.delete(id);
    }
}
