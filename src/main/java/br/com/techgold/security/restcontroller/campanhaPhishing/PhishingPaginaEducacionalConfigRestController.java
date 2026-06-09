package br.com.techgold.security.restcontroller.campanhaPhishing;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.techgold.security.dto.DtoPhishingPaginaEducacionalConfig;
import br.com.techgold.security.model.campanhaPhishing.PhishingPaginaEducacionalConfig;
import br.com.techgold.security.services.campanhaPhishing.PhishingPaginaEducacionalConfigService;

@RestController
@RequestMapping("api/phishing/pagina-config")
public class PhishingPaginaEducacionalConfigRestController {

    @Autowired
    private PhishingPaginaEducacionalConfigService service;

    /** Endpoint público — acessado pela phishingEducacional.html sem autenticação */
    @GetMapping("/ativa")
    public ResponseEntity<DtoPhishingPaginaEducacionalConfig> buscarAtiva() {
        return service.buscarAtiva()
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.noContent().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping
    public ResponseEntity<List<DtoPhishingPaginaEducacionalConfig>> listar() {
        return ResponseEntity.ok(service.listarTodos().stream().map(this::toDto).toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DtoPhishingPaginaEducacionalConfig> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(c -> ResponseEntity.ok(toDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> dados) {
        try {
            return ResponseEntity.ok(toDto(service.salvar(mapFromDados(new PhishingPaginaEducacionalConfig(), dados))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        Optional<PhishingPaginaEducacionalConfig> opt = service.buscarPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        try {
            return ResponseEntity.ok(toDto(service.salvar(mapFromDados(opt.get(), dados))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @PostMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        if (!service.existePorId(id)) return ResponseEntity.notFound().build();
        service.ativar(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!service.existePorId(id)) return ResponseEntity.notFound().build();
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private PhishingPaginaEducacionalConfig mapFromDados(PhishingPaginaEducacionalConfig c, Map<String, Object> d) {
        str(d, "nome",                c::setNome);
        str(d, "nomeEmpresa",         c::setNomeEmpresa);
        str(d, "heroBadgeText",       c::setHeroBadgeText);
        str(d, "heroTitulo",          c::setHeroTitulo);
        str(d, "heroTituloDestaque",  c::setHeroTituloDestaque);
        str(d, "heroSubtitulo",       c::setHeroSubtitulo);
        str(d, "heroTextoSeguranca",  c::setHeroTextoSeguranca);
        str(d, "textoExplicacao",     c::setTextoExplicacao);
        str(d, "textoFooter",         c::setTextoFooter);
        str(d, "stat1Valor",          c::setStat1Valor);
        str(d, "stat1Label",          c::setStat1Label);
        str(d, "stat1Cor",            c::setStat1Cor);
        str(d, "stat2Valor",          c::setStat2Valor);
        str(d, "stat2Label",          c::setStat2Label);
        str(d, "stat2Cor",            c::setStat2Cor);
        str(d, "stat3Valor",          c::setStat3Valor);
        str(d, "stat3Label",          c::setStat3Label);
        str(d, "stat3Cor",            c::setStat3Cor);
        str(d, "tip1Titulo",          c::setTip1Titulo);
        str(d, "tip1Texto",           c::setTip1Texto);
        str(d, "tip2Titulo",          c::setTip2Titulo);
        str(d, "tip2Texto",           c::setTip2Texto);
        str(d, "tip3Titulo",          c::setTip3Titulo);
        str(d, "tip3Texto",           c::setTip3Texto);
        str(d, "tip4Titulo",          c::setTip4Titulo);
        str(d, "tip4Texto",           c::setTip4Texto);
        str(d, "acaoTitulo",          c::setAcaoTitulo);
        str(d, "acaoPasso1",          c::setAcaoPasso1);
        str(d, "acaoPasso2",          c::setAcaoPasso2);
        str(d, "acaoPasso3",          c::setAcaoPasso3);
        str(d, "acaoPasso4",          c::setAcaoPasso4);
        str(d, "corDestaque",         c::setCorDestaque);
        str(d, "corHero1",            c::setCorHero1);
        str(d, "corHero2",            c::setCorHero2);
        bool(d, "exibirStats",        c::setExibirStats);
        bool(d, "exibirExplicacao",   c::setExibirExplicacao);
        bool(d, "exibirTips",         c::setExibirTips);
        bool(d, "exibirAcao",         c::setExibirAcao);
        bool(d, "ativo",              c::setAtivo);
        return c;
    }

    private void str(Map<String, Object> d, String key, java.util.function.Consumer<String> setter) {
        if (d.containsKey(key) && d.get(key) != null) setter.accept(d.get(key).toString());
    }

    private void bool(Map<String, Object> d, String key, java.util.function.Consumer<Boolean> setter) {
        if (d.containsKey(key) && d.get(key) != null) setter.accept(Boolean.parseBoolean(d.get(key).toString()));
    }

    private DtoPhishingPaginaEducacionalConfig toDto(PhishingPaginaEducacionalConfig c) {
        return new DtoPhishingPaginaEducacionalConfig(
                c.getId(), c.getNome(), c.getNomeEmpresa(),
                c.getHeroBadgeText(), c.getHeroTitulo(), c.getHeroTituloDestaque(),
                c.getHeroSubtitulo(), c.getHeroTextoSeguranca(),
                c.getTextoExplicacao(), c.getTextoFooter(),
                c.getStat1Valor(), c.getStat1Label(), c.getStat1Cor(),
                c.getStat2Valor(), c.getStat2Label(), c.getStat2Cor(),
                c.getStat3Valor(), c.getStat3Label(), c.getStat3Cor(),
                c.getTip1Titulo(), c.getTip1Texto(),
                c.getTip2Titulo(), c.getTip2Texto(),
                c.getTip3Titulo(), c.getTip3Texto(),
                c.getTip4Titulo(), c.getTip4Texto(),
                c.getAcaoTitulo(),
                c.getAcaoPasso1(), c.getAcaoPasso2(), c.getAcaoPasso3(), c.getAcaoPasso4(),
                c.getCorDestaque(), c.getCorHero1(), c.getCorHero2(),
                c.isExibirStats(), c.isExibirExplicacao(), c.isExibirTips(), c.isExibirAcao(),
                c.isAtivo()
        );
    }
}
