package prova.appnativo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import prova.appnativo.dto.ProfessorRequest;
import prova.appnativo.dto.ProfessorResponse;
import prova.appnativo.service.ProfessorService;

import java.util.List;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessorResponse create(@Valid @RequestBody ProfessorRequest request) {
        return professorService.create(request);
    }

    @GetMapping
    public List<ProfessorResponse> findAll(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Boolean ativo
    ) {
        return professorService.findAll(busca, ativo);
    }

    @GetMapping("/{id}")
    public ProfessorResponse findById(@PathVariable Long id) {
        return professorService.findById(id);
    }

    @PutMapping("/{id}")
    public ProfessorResponse update(@PathVariable Long id, @Valid @RequestBody ProfessorRequest request) {
        return professorService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public ProfessorResponse alternarStatus(@PathVariable Long id) {
        return professorService.alternarStatus(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        professorService.delete(id);
    }
}
