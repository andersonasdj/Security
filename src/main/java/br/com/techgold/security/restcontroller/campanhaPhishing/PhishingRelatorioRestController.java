package br.com.techgold.security.restcontroller.campanhaPhishing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.techgold.security.dto.DtoCampanhaResumo;
import br.com.techgold.security.orm.campanhaPhishing.AlvoCliqueProjecao;
import br.com.techgold.security.orm.campanhaPhishing.CampanhaResumoProjecao;
import br.com.techgold.security.services.campanhaPhishing.CampanhaPhishingService;

@RestController
@RequestMapping("api/phishing/relatorios")
public class PhishingRelatorioRestController {

    @Autowired private CampanhaPhishingService campanhaService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/resumo")
    public ResponseEntity<List<DtoCampanhaResumo>> resumo() {
        return ResponseEntity.ok(campanhaService.buscarResumo().stream().map(this::toDto).toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{campanhaId}")
    public ResponseEntity<?> detalhe(@PathVariable Long campanhaId) {
        if (!campanhaService.existePorId(campanhaId)) return ResponseEntity.notFound().build();

        CampanhaResumoProjecao resumo = campanhaService.buscarResumoPorId(campanhaId);
        List<AlvoCliqueProjecao> alvos = campanhaService.buscarCliquesPorCampanha(campanhaId);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("resumo", toDto(resumo));
        resposta.put("alvos", alvos.stream().map(a -> {
            Map<String, Object> item = new HashMap<>();
            item.put("nome", a.getNome());
            item.put("email", a.getEmail());
            item.put("enviado", a.isEnviado());
            item.put("clicou", a.isClicou());
            item.put("dataEnvio", a.getDataEnvio());
            item.put("dataClique", a.getDataClique());
            return item;
        }).toList());

        return ResponseEntity.ok(resposta);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/{campanhaId}/executivo")
    public ResponseEntity<?> relatorioExecutivo(@PathVariable Long campanhaId) {
        return campanhaService.buscarPorId(campanhaId).map(campanha -> {
            CampanhaResumoProjecao resumo = campanhaService.buscarResumoPorId(campanhaId);
            List<AlvoCliqueProjecao> alvos = campanhaService.buscarCliquesPorCampanha(campanhaId);

            long totalAlvos = resumo.getTotalAlvos() != null ? resumo.getTotalAlvos() : 0;
            long enviados   = resumo.getEnviados()   != null ? resumo.getEnviados()   : 0;
            long clicados   = resumo.getClicados()   != null ? resumo.getClicados()   : 0;
            long naoClicados = enviados - clicados;
            double taxa = totalAlvos > 0 ? Math.round((double) clicados / totalAlvos * 1000.0) / 10.0 : 0.0;

            Map<String, Object> campInfo = new HashMap<>();
            campInfo.put("id",            campanha.getId());
            campInfo.put("nome",          campanha.getNome());
            campInfo.put("descricao",     campanha.getDescricao());
            campInfo.put("cliente",       campanha.getCliente().getNomeCliente());
            campInfo.put("status",        campanha.getStatus().toString());
            campInfo.put("criadoPor",     campanha.getCriadoPor());
            campInfo.put("dataCriacao",   campanha.getDataCriacao());
            campInfo.put("dataInicio",    campanha.getDataInicio());
            campInfo.put("dataConclusao", campanha.getDataConclusao());

            Map<String, Object> modeloInfo = null;
            if (campanha.getModeloEmail() != null) {
                var m = campanha.getModeloEmail();
                modeloInfo = new HashMap<>();
                modeloInfo.put("nome",      m.getNome());
                modeloInfo.put("assunto",   m.getAssunto());
                modeloInfo.put("categoria", m.getCategoria());
                modeloInfo.put("corpoHtml", m.getCorpoHtml());
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalAlvos",  totalAlvos);
            stats.put("enviados",    enviados);
            stats.put("clicados",    clicados);
            stats.put("naoClicados", Math.max(naoClicados, 0));
            stats.put("taxaClique",  taxa);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("campanha",     campInfo);
            resposta.put("modeloEmail",  modeloInfo);
            resposta.put("estatisticas", stats);
            resposta.put("alvos", alvos.stream().map(a -> {
                Map<String, Object> item = new HashMap<>();
                item.put("nome",      a.getNome());
                item.put("email",     a.getEmail());
                item.put("enviado",   a.isEnviado());
                item.put("clicou",    a.isClicou());
                item.put("dataEnvio", a.getDataEnvio());
                item.put("dataClique",a.getDataClique());
                return item;
            }).toList());

            return ResponseEntity.ok(resposta);
        }).orElse(ResponseEntity.notFound().build());
    }

    private DtoCampanhaResumo toDto(CampanhaResumoProjecao p) {
        long total = p.getTotalAlvos() != null ? p.getTotalAlvos() : 0;
        long clicados = p.getClicados() != null ? p.getClicados() : 0;
        double taxa = total > 0 ? Math.round((double) clicados / total * 1000.0) / 10.0 : 0.0;

        return new DtoCampanhaResumo(
                p.getId(),
                p.getNome(),
                p.getStatus().toString(),
                total,
                p.getEnviados() != null ? p.getEnviados() : 0,
                clicados,
                taxa);
    }
}
