package br.com.techgold.security.controller.campanhaPhishing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("phishing")
public class CampanhaPhishingController {

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/campanhas")
    public String campanhas() {
        return "phishingCampanhas.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/modelos")
    public String modelos() {
        return "phishingModeloEmail.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/relatorios")
    public String relatorios() {
        return "phishingRelatorios.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/config")
    public String config() {
        return "phishingConfig.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/educacional-config")
    public String educacionalConfig() {
        return "phishingEducacionalConfig.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/relatorio-executivo")
    public String relatorioExecutivo() {
        return "phishingRelatorioExecutivo.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SADMIN')")
    @GetMapping("/contatos")
    public String contatos() {
        return "phishingContatos.html";
    }
}
