package prova.appnativo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import prova.appnativo.dto.PlanoRequest;
import prova.appnativo.dto.PlanoResponse;
import prova.appnativo.service.PlanoService;

import java.util.List;

@RestController
@RequestMapping("/planos")
public class PlanoController {

    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanoResponse create(@Valid @RequestBody PlanoRequest request) {
        return planoService.create(request);
    }

    @GetMapping
    public List<PlanoResponse> findAll(@RequestParam(required = false) Boolean ativo) {
        return planoService.findAll(ativo);
    }

    @GetMapping("/{id}")
    public PlanoResponse findById(@PathVariable Long id) {
        return planoService.findById(id);
    }

    @PutMapping("/{id}")
    public PlanoResponse update(@PathVariable Long id, @Valid @RequestBody PlanoRequest request) {
        return planoService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public PlanoResponse alternarStatus(@PathVariable Long id) {
        return planoService.alternarStatus(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        planoService.delete(id);
    }
}
