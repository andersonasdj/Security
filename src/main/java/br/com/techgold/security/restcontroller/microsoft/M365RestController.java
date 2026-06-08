package br.com.techgold.security.restcontroller.microsoft;

import br.com.techgold.security.dto.DtoM365CaixaPostal;
import br.com.techgold.security.dto.DtoM365Config;
import br.com.techgold.security.model.Cliente;
import br.com.techgold.security.model.microsoft.M365ClienteConfig;
import br.com.techgold.security.repository.ClienteRepository;
import br.com.techgold.security.services.microsoft.M365ConfigService;
import br.com.techgold.security.services.microsoft.M365GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/m365")
public class M365RestController {

    @Autowired private M365ConfigService configService;
    @Autowired private M365GraphService graphService;
    @Autowired private ClienteRepository clienteRepository;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/configs")
    public ResponseEntity<List<DtoM365Config>> listarConfigs() {
        var lista = configService.listarTodos().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/configs/{id}")
    public ResponseEntity<DtoM365Config> buscarConfig(@PathVariable Long id) {
        return configService.buscarPorId(id)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/configs/cliente/{clienteId}")
    public ResponseEntity<List<DtoM365Config>> buscarPorCliente(@PathVariable Long clienteId) {
        var lista = configService.buscarPorCliente(clienteId).stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping("/configs")
    public ResponseEntity<?> criarConfig(@RequestBody Map<String, Object> dados) {
        try {
            Long clienteId = Long.valueOf(dados.get("clienteId").toString());
            Cliente cliente = clienteRepository.getReferenceById(clienteId);

            var config = new M365ClienteConfig();
            config.setCliente(cliente);
            config.setNome(dados.get("nome").toString());
            config.setTenantId(dados.get("tenantId").toString());
            config.setClientId(dados.get("clientId").toString());
            config.setClientSecret(dados.get("clientSecret").toString());
            config.setAtivo(Boolean.parseBoolean(dados.getOrDefault("ativo", "true").toString()));
            config.setMonitorCaixa(Boolean.parseBoolean(dados.getOrDefault("monitorCaixa", "false").toString()));

            return ResponseEntity.ok(toDto(configService.salvar(config)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PutMapping("/configs/{id}")
    public ResponseEntity<?> atualizarConfig(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        var opt = configService.buscarPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            var config = opt.get();
            if (dados.containsKey("nome")) config.setNome(dados.get("nome").toString());
            if (dados.containsKey("tenantId")) config.setTenantId(dados.get("tenantId").toString());
            if (dados.containsKey("clientId") && !dados.get("clientId").toString().isBlank())
                config.setClientId(dados.get("clientId").toString());
            if (dados.containsKey("clientSecret") && !dados.get("clientSecret").toString().isBlank())
                config.setClientSecret(dados.get("clientSecret").toString());
            if (dados.containsKey("ativo"))
                config.setAtivo(Boolean.parseBoolean(dados.get("ativo").toString()));
            if (dados.containsKey("monitorCaixa"))
                config.setMonitorCaixa(Boolean.parseBoolean(dados.get("monitorCaixa").toString()));
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
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Void> deletarConfig(@PathVariable Long id) {
        if (!configService.existePorId(id)) return ResponseEntity.notFound().build();
        configService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/diagnostico/{configId}")
    public ResponseEntity<?> diagnostico(@PathVariable Long configId) {
        var opt = configService.buscarPorId(configId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        try {
            String csv = graphService.buscarCSVBruto(opt.get());
            String[] linhas = csv.split("[\r\n]+", 3);
            String header = linhas.length > 0 ? linhas[0] : "";
            // Remove BOM se presente
            if (header.startsWith("﻿")) header = header.substring(1);
            List<String> colunas = List.of(header.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
            return ResponseEntity.ok(Map.of(
                    "colunas", colunas,
                    "primeiraLinha", linhas.length > 1 ? linhas[1] : "(sem dados)",
                    "totalCaracteres", csv.length()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/relatorio/{configId}")
    public ResponseEntity<?> buscarRelatorio(@PathVariable Long configId) {
        var opt = configService.buscarPorId(configId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            var caixas = graphService.buscarCaixasPostais(opt.get());
            List<DtoM365CaixaPostal> resultado = caixas.stream()
                    .map(c -> {
                        double pct = c.storageLimitBytes() > 0
                                ? Math.round((double) c.storageUsedBytes() / c.storageLimitBytes() * 1000.0) / 10.0
                                : 0.0;
                        return new DtoM365CaixaPostal(
                                c.userPrincipalName(),
                                c.displayName(),
                                c.totalItemCount(),
                                c.storageUsedBytes(),
                                c.storageLimitBytes(),
                                c.deleted(),
                                pct,
                                c.mailboxType(),
                                c.department(),
                                c.createdDate(),
                                c.lastActivityDate(),
                                c.accountEnabled(),
                                c.licencaSkuIds(),
                                c.ultimaTrocaSenha());
                    })
                    .sorted((a, b) -> Double.compare(b.percentualUso(), a.percentualUso()))
                    .toList();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    private DtoM365Config toDto(M365ClienteConfig c) {
        return new DtoM365Config(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNomeCliente(),
                c.getNome(),
                c.getTenantId(),
                c.getClientId(),
                c.isAtivo(),
                c.isMonitorCaixa());
    }
}
