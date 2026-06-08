package br.com.techgold.security.restcontroller.campanhaPhishing;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.techgold.security.dto.DtoModeloEmailPhishing;
import br.com.techgold.security.model.campanhaPhishing.ModeloEmailPhishing;
import br.com.techgold.security.services.campanhaPhishing.ModeloEmailPhishingService;

@RestController
@RequestMapping("api/phishing/modelos")
public class ModeloEmailPhishingRestController {

    @Autowired private ModeloEmailPhishingService modeloService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping
    public ResponseEntity<List<DtoModeloEmailPhishing>> listar() {
        return ResponseEntity.ok(modeloService.listarTodos().stream().map(this::toDto).toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DtoModeloEmailPhishing> buscar(@PathVariable Long id) {
        return modeloService.buscarPorId(id)
                .map(m -> ResponseEntity.ok(toDto(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> dados) {
        try {
            var modelo = new ModeloEmailPhishing();
            modelo.setNome(dados.get("nome").toString());
            modelo.setAssunto(dados.get("assunto").toString());
            modelo.setCorpoHtml(dados.get("corpoHtml").toString());
            modelo.setCategoria(dados.getOrDefault("categoria", "").toString());
            modelo.setAtivo(Boolean.parseBoolean(dados.getOrDefault("ativo", "true").toString()));

            return ResponseEntity.ok(toDto(modeloService.salvar(modelo)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        var opt = modeloService.buscarPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            var modelo = opt.get();
            if (dados.containsKey("nome")) modelo.setNome(dados.get("nome").toString());
            if (dados.containsKey("assunto")) modelo.setAssunto(dados.get("assunto").toString());
            if (dados.containsKey("corpoHtml")) modelo.setCorpoHtml(dados.get("corpoHtml").toString());
            if (dados.containsKey("categoria")) modelo.setCategoria(dados.get("categoria").toString());
            if (dados.containsKey("ativo")) modelo.setAtivo(Boolean.parseBoolean(dados.get("ativo").toString()));
            return ResponseEntity.ok(toDto(modeloService.salvar(modelo)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!modeloService.existePorId(id)) return ResponseEntity.notFound().build();
        modeloService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private DtoModeloEmailPhishing toDto(ModeloEmailPhishing m) {
        return new DtoModeloEmailPhishing(
                m.getId(),
                m.getNome(),
                m.getAssunto(),
                m.getCorpoHtml(),
                m.getCategoria(),
                m.getDataCriacao(),
                m.isAtivo());
    }
}
