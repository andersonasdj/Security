package br.com.techgold.security.controller.campanhaPhishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.techgold.security.services.campanhaPhishing.RastreamentoPhishingService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RastreamentoPhishingController {

    @Autowired
    private RastreamentoPhishingService rastreamentoService;

    @GetMapping("/phishing/click/{token}")
    public String registrarClique(@PathVariable String token, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();

        rastreamentoService.registrarClique(token, ip, request.getHeader("User-Agent"));

        return "redirect:/phishing/phishingEducacional.html";
    }
}
