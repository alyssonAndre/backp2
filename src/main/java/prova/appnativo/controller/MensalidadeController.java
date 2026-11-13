package prova.appnativo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import prova.appnativo.domain.enums.StatusMensalidade;
import prova.appnativo.dto.GerarMensalidadesRequest;
import prova.appnativo.dto.MensalidadeRequest;
import prova.appnativo.dto.MensalidadeResponse;
import prova.appnativo.dto.PagamentoRequest;
import prova.appnativo.service.MensalidadeService;

import java.util.List;

@RestController
@RequestMapping("/mensalidades")
public class MensalidadeController {

    private final MensalidadeService mensalidadeService;

    public MensalidadeController(MensalidadeService mensalidadeService) {
        this.mensalidadeService = mensalidadeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensalidadeResponse create(@Valid @RequestBody MensalidadeRequest request) {
        return mensalidadeService.create(request);
    }

    @PostMapping("/gerar")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MensalidadeResponse> gerar(@Valid @RequestBody GerarMensalidadesRequest request) {
        return mensalidadeService.gerar(request);
    }

    @GetMapping
    public List<MensalidadeResponse> findAll(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) StatusMensalidade status
    ) {
        return mensalidadeService.findAll(alunoId, status);
    }

    @GetMapping("/{id}")
    public MensalidadeResponse findById(@PathVariable Long id) {
        return mensalidadeService.findById(id);
    }

    @PutMapping("/{id}")
    public MensalidadeResponse update(@PathVariable Long id, @Valid @RequestBody MensalidadeRequest request) {
        return mensalidadeService.update(id, request);
    }

    @PatchMapping("/{id}/pagamento")
    public MensalidadeResponse registrarPagamento(@PathVariable Long id, @Valid @RequestBody PagamentoRequest request) {
        return mensalidadeService.registrarPagamento(id, request);
    }

    @PatchMapping("/{id}/estorno")
    public MensalidadeResponse estornarPagamento(@PathVariable Long id) {
        return mensalidadeService.estornarPagamento(id);
    }

    @PatchMapping("/{id}/cancelamento")
    public MensalidadeResponse cancelar(@PathVariable Long id) {
        return mensalidadeService.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        mensalidadeService.delete(id);
    }
}
