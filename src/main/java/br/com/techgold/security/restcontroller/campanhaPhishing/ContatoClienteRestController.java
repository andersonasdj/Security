package br.com.techgold.security.restcontroller.campanhaPhishing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.techgold.security.dto.DtoContatoCliente;
import br.com.techgold.security.services.ClienteService;
import br.com.techgold.security.services.campanhaPhishing.ContatoClienteService;

@RestController
@RequestMapping("api/phishing/contatos")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
public class ContatoClienteRestController {

    @Autowired private ContatoClienteService service;
    @Autowired private ClienteService clienteService;

    /** Lista contatos de um cliente */
    @GetMapping
    public ResponseEntity<List<DtoContatoCliente>> listar(@RequestParam Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    /** Lista clientes ativos para o dropdown */
    @GetMapping("/clientes")
    public ResponseEntity<List<Map<String, Object>>> listarClientes() {
        List<Map<String, Object>> lista = clienteService.listarAtivos().stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.id());
            m.put("nome", c.nomeCliente());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    /** Busca contato por ID */
    @GetMapping("/{id}")
    public ResponseEntity<DtoContatoCliente> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Cria novo contato */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body) {
        Long clienteId = toLong(body.get("clienteId"));
        String nome    = str(body.get("nome"));
        String email   = str(body.get("email"));

        if (clienteId == null || nome.isBlank() || email.isBlank())
            return ResponseEntity.badRequest().body("clienteId, nome e email são obrigatórios");

        var contato = service.salvar(clienteId, nome, email);
        return ResponseEntity.ok(toDto(contato));
    }

    /** Atualiza contato existente */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String nome  = str(body.get("nome"));
        String email = str(body.get("email"));
        boolean ativo = body.get("ativo") instanceof Boolean b ? b : true;

        if (nome.isBlank() || email.isBlank())
            return ResponseEntity.badRequest().body("nome e email são obrigatórios");

        return service.atualizar(id, nome, email, ativo)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Remove contato */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!service.existePorId(id)) return ResponseEntity.notFound().build();
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /** Importa contatos via arquivo CSV (multipart) */
    @PostMapping("/importar")
    public ResponseEntity<?> importar(
            @RequestParam Long clienteId,
            @RequestParam MultipartFile arquivo) {
        try (var reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> linhas = reader.lines().collect(Collectors.toList());
            return ResponseEntity.ok(service.importar(clienteId, linhas));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    /** Importa contatos colados como texto puro (JSON body com campo "texto") */
    @PostMapping("/importar-texto")
    public ResponseEntity<?> importarTexto(@RequestBody Map<String, Object> body) {
        Long clienteId = toLong(body.get("clienteId"));
        String texto   = str(body.get("texto"));
        if (clienteId == null || texto.isBlank())
            return ResponseEntity.badRequest().body("clienteId e texto são obrigatórios");

        List<String> linhas = List.of(texto.split("\\r?\\n"));
        return ResponseEntity.ok(service.importar(clienteId, linhas));
    }

    /** Exporta contatos do cliente como CSV para download */
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam Long clienteId) {
        var contatos = service.listarPorCliente(clienteId);
        StringBuilder sb = new StringBuilder("Nome,E-mail,Ativo\r\n");
        contatos.forEach(c -> sb
                .append(escapeCsv(c.nome())).append(',')
                .append(escapeCsv(c.email())).append(',')
                .append(c.ativo() ? "Sim" : "Não").append("\r\n"));

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contatos_cliente_" + clienteId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private DtoContatoCliente toDto(br.com.techgold.security.model.campanhaPhishing.ContatoCliente c) {
        return new DtoContatoCliente(
                c.getId(), c.getNome(), c.getEmail(),
                c.getCliente().getId(), c.getCliente().getNomeCliente(),
                c.isAtivo(),
                c.getDataCriacao() != null ? c.getDataCriacao().toString() : null);
    }

    private String str(Object o) { return o instanceof String s ? s.strip() : ""; }

    private Long toLong(Object o) {
        if (o instanceof Number n) return n.longValue();
        try { return o != null ? Long.parseLong(o.toString()) : null; } catch (Exception e) { return null; }
    }

    private String escapeCsv(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n"))
            return "\"" + v.replace("\"", "\"\"") + "\"";
        return v;
    }
}
