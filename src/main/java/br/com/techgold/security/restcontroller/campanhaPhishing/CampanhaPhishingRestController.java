package br.com.techgold.security.restcontroller.campanhaPhishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import br.com.techgold.security.dto.DtoCampanhaPhishing;
import br.com.techgold.security.model.Cliente;
import br.com.techgold.security.model.campanhaPhishing.CampanhaPhishing;
import br.com.techgold.security.model.campanhaPhishing.enums.StatusCampanha;
import br.com.techgold.security.repository.ClienteRepository;
import br.com.techgold.security.services.campanhaPhishing.CampanhaPhishingService;
import br.com.techgold.security.services.campanhaPhishing.EnvioPhishingService;
import br.com.techgold.security.services.campanhaPhishing.ModeloEmailPhishingService;
import br.com.techgold.security.services.campanhaPhishing.PhishingConfigSmtpService;

@RestController
@RequestMapping("api/phishing/campanhas")
public class CampanhaPhishingRestController {

    @Autowired private CampanhaPhishingService campanhaService;
    @Autowired private ModeloEmailPhishingService modeloService;
    @Autowired private PhishingConfigSmtpService configService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EnvioPhishingService envioService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping
    public ResponseEntity<List<DtoCampanhaPhishing>> listar() {
        return ResponseEntity.ok(campanhaService.listarTodas().stream().map(this::toDto).toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DtoCampanhaPhishing> buscar(@PathVariable Long id) {
        return campanhaService.buscarPorId(id)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{id}/alvos")
    public ResponseEntity<?> listarAlvos(@PathVariable Long id) {
        if (!campanhaService.existePorId(id)) return ResponseEntity.notFound().build();
        var alvos = campanhaService.buscarAlvosPorCampanha(id).stream()
                .map(a -> Map.of(
                        "id", a.getId(),
                        "nome", a.getNome(),
                        "email", a.getEmail(),
                        "enviado", a.isEnviado(),
                        "clicou", a.isClicou()))
                .toList();
        return ResponseEntity.ok(alvos);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> dados, Authentication auth) {
        try {
            Long clienteId = Long.valueOf(dados.get("clienteId").toString());
            Cliente cliente = clienteRepository.getReferenceById(clienteId);

            Long modeloId = Long.valueOf(dados.get("modeloEmailId").toString());
            var modelo = modeloService.buscarPorId(modeloId)
                    .orElseThrow(() -> new IllegalArgumentException("Modelo de e-mail não encontrado"));

            Long configId = Long.valueOf(dados.get("configSmtpId").toString());
            var config = configService.buscarPorId(configId)
                    .orElseThrow(() -> new IllegalArgumentException("Configuração SMTP não encontrada"));

            var campanha = new CampanhaPhishing();
            campanha.setNome(dados.get("nome").toString());
            campanha.setDescricao(dados.getOrDefault("descricao", "").toString());
            campanha.setCliente(cliente);
            campanha.setModeloEmail(modelo);
            campanha.setConfigSmtp(config);
            campanha.setCriadoPor(auth != null ? auth.getName() : "Sistema");
            campanha = campanhaService.salvar(campanha);

            registrarAlvos(campanha, dados.get("alvos"));

            return ResponseEntity.ok(toDto(campanhaService.buscarPorId(campanha.getId()).orElseThrow()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        var opt = campanhaService.buscarPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            var campanha = opt.get();
            if (campanha.getStatus() != StatusCampanha.RASCUNHO) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Somente campanhas em rascunho podem ser editadas"));
            }
            if (dados.containsKey("nome")) campanha.setNome(dados.get("nome").toString());
            if (dados.containsKey("descricao")) campanha.setDescricao(dados.get("descricao").toString());
            if (dados.containsKey("modeloEmailId")) {
                Long modeloId = Long.valueOf(dados.get("modeloEmailId").toString());
                campanha.setModeloEmail(modeloService.buscarPorId(modeloId)
                        .orElseThrow(() -> new IllegalArgumentException("Modelo de e-mail não encontrado")));
            }
            if (dados.containsKey("configSmtpId")) {
                Long configId = Long.valueOf(dados.get("configSmtpId").toString());
                campanha.setConfigSmtp(configService.buscarPorId(configId)
                        .orElseThrow(() -> new IllegalArgumentException("Configuração SMTP não encontrada")));
            }
            if (dados.containsKey("clienteId")) {
                Long clienteId = Long.valueOf(dados.get("clienteId").toString());
                campanha.setCliente(clienteRepository.getReferenceById(clienteId));
            }
            campanha = campanhaService.salvar(campanha);

            if (dados.containsKey("alvos")) {
                registrarAlvos(campanha, dados.get("alvos"));
            }

            return ResponseEntity.ok(toDto(campanhaService.buscarPorId(campanha.getId()).orElseThrow()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping("/{id}/enviar")
    public ResponseEntity<?> disparar(@PathVariable Long id) {
        var opt = campanhaService.buscarPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var campanha = opt.get();
        if (campanha.getStatus() != StatusCampanha.RASCUNHO && campanha.getStatus() != StatusCampanha.AGENDADA) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Campanha já foi disparada"));
        }
        if (campanhaService.buscarAlvosPorCampanha(id).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Campanha não possui alvos cadastrados"));
        }

        envioService.disparar(campanha.getId());
        return ResponseEntity.accepted().body(Map.of("mensagem", "Disparo de campanha iniciado"));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!campanhaService.existePorId(id)) return ResponseEntity.notFound().build();
        campanhaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private void registrarAlvos(CampanhaPhishing campanha, Object alvosBrutos) {
        if (!(alvosBrutos instanceof List<?> lista)) return;

        List<String[]> alvos = new ArrayList<>();
        for (Object item : lista) {
            if (item instanceof Map<?, ?> mapa) {
                Object nome = mapa.get("nome");
                Object email = mapa.get("email");
                if (nome != null && email != null && !email.toString().isBlank()) {
                    alvos.add(new String[] { nome.toString(), email.toString() });
                }
            }
        }
        if (!alvos.isEmpty()) campanhaService.definirAlvos(campanha, alvos);
    }

    private DtoCampanhaPhishing toDto(CampanhaPhishing c) {
        long totalAlvos = campanhaService.buscarAlvosPorCampanha(c.getId()).size();
        return new DtoCampanhaPhishing(
                c.getId(),
                c.getNome(),
                c.getDescricao(),
                c.getCliente().getId(),
                c.getCliente().getNomeCliente(),
                c.getModeloEmail().getId(),
                c.getModeloEmail().getNome(),
                c.getConfigSmtp().getId(),
                c.getConfigSmtp().getNome(),
                c.getStatus().toString(),
                c.getDataCriacao(),
                c.getDataInicio(),
                c.getDataConclusao(),
                c.getCriadoPor(),
                totalAlvos);
    }
}
