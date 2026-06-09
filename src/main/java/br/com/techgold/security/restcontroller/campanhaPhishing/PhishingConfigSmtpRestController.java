package br.com.techgold.security.restcontroller.campanhaPhishing;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.techgold.security.dto.DtoPhishingConfigSmtp;
import br.com.techgold.security.model.Cliente;
import br.com.techgold.security.model.campanhaPhishing.PhishingConfigSmtp;
import br.com.techgold.security.repository.ClienteRepository;
import br.com.techgold.security.services.campanhaPhishing.PhishingConfigSmtpService;

@RestController
@RequestMapping("api/phishing/configs")
public class PhishingConfigSmtpRestController {

    @Autowired private PhishingConfigSmtpService configService;
    @Autowired private ClienteRepository clienteRepository;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping
    public ResponseEntity<List<DtoPhishingConfigSmtp>> listar() {
        return ResponseEntity.ok(configService.listarTodos().stream().map(this::toDto).toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DtoPhishingConfigSmtp> buscar(@PathVariable Long id) {
        return configService.buscarPorId(id)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> dados) {
        try {
            Long clienteId = Long.valueOf(dados.get("clienteId").toString());
            Cliente cliente = clienteRepository.getReferenceById(clienteId);

            var config = new PhishingConfigSmtp();
            config.setCliente(cliente);
            config.setNome(dados.get("nome").toString());
            config.setHost(dados.get("host").toString());
            config.setPorta(Integer.parseInt(dados.get("porta").toString()));
            config.setUsuario(dados.get("usuario").toString());
            config.setSenha(dados.get("senha").toString());
            config.setRemetenteNome(dados.get("remetenteNome").toString());
            config.setRemetenteEmail(dados.get("remetenteEmail").toString());
            config.setUsarTls(Boolean.parseBoolean(dados.getOrDefault("usarTls", "true").toString()));
            config.setAtivo(Boolean.parseBoolean(dados.getOrDefault("ativo", "true").toString()));
            config.setTamanheLote(toInt(dados.getOrDefault("tamanheLote", "10"), 10));
            config.setIntervaloSegundos(toInt(dados.getOrDefault("intervaloSegundos", "30"), 30));

            return ResponseEntity.ok(toDto(configService.salvar(config)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        var opt = configService.buscarPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            var config = opt.get();
            if (dados.containsKey("nome")) config.setNome(dados.get("nome").toString());
            if (dados.containsKey("host")) config.setHost(dados.get("host").toString());
            if (dados.containsKey("porta")) config.setPorta(Integer.parseInt(dados.get("porta").toString()));
            if (dados.containsKey("usuario")) config.setUsuario(dados.get("usuario").toString());
            if (dados.containsKey("senha") && !dados.get("senha").toString().isBlank())
                config.setSenha(dados.get("senha").toString());
            if (dados.containsKey("remetenteNome")) config.setRemetenteNome(dados.get("remetenteNome").toString());
            if (dados.containsKey("remetenteEmail")) config.setRemetenteEmail(dados.get("remetenteEmail").toString());
            if (dados.containsKey("usarTls")) config.setUsarTls(Boolean.parseBoolean(dados.get("usarTls").toString()));
            if (dados.containsKey("ativo")) config.setAtivo(Boolean.parseBoolean(dados.get("ativo").toString()));
            if (dados.containsKey("tamanheLote")) config.setTamanheLote(toInt(dados.get("tamanheLote"), 10));
            if (dados.containsKey("intervaloSegundos")) config.setIntervaloSegundos(toInt(dados.get("intervaloSegundos"), 30));
            if (dados.containsKey("clienteId")) {
                Long clienteId = Long.valueOf(dados.get("clienteId").toString());
                config.setCliente(clienteRepository.getReferenceById(clienteId));
            }
            return ResponseEntity.ok(toDto(configService.salvar(config)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!configService.existePorId(id)) return ResponseEntity.notFound().build();
        configService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private DtoPhishingConfigSmtp toDto(PhishingConfigSmtp c) {
        return new DtoPhishingConfigSmtp(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNomeCliente(),
                c.getNome(),
                c.getHost(),
                c.getPorta(),
                c.getUsuario(),
                c.getRemetenteNome(),
                c.getRemetenteEmail(),
                c.isUsarTls(),
                c.isAtivo(),
                c.getTamanheLote(),
                c.getIntervaloSegundos());
    }

    private int toInt(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        try { return Integer.parseInt(value.toString()); } catch (NumberFormatException e) { return defaultValue; }
    }
}
