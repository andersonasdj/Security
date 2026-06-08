package br.com.techgold.security.restcontroller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.security.dto.DtoEmails;
import br.com.techgold.security.dto.DtoLogAcesso;
import br.com.techgold.security.dto.DtoLogin;
import br.com.techgold.security.dto.DtoPaises;
import br.com.techgold.security.model.ConfiguracaoEmail;
import br.com.techgold.security.model.ConfiguracaoPaises;
import br.com.techgold.security.services.ConfiguracaoEmailService;
import br.com.techgold.security.services.ConfiguracaoPaisesService;
import br.com.techgold.security.services.LogLoginService;

@RestController
@RequestMapping("sistema")
public class AppRestController {
	
	@Autowired private LogLoginService loginService;
	
	@Autowired private ConfiguracaoPaisesService paisesService;
	
	@Autowired private ConfiguracaoEmailService emailService;
	
	@GetMapping("/logs")
	public List<DtoLogAcesso> logar() {
		return loginService.listarLogs();
	}
	
	@GetMapping("/timeline/log/id/{id}/{inicio}/{fim}")
	public DtoLogin timelinePorFuncionarioPeriodo(@PathVariable Long id, @PathVariable LocalDate inicio, @PathVariable LocalDate fim) {
		return loginService.buscarPrimeiroLogin(id, inicio, fim);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/configuracao/paises")
	public ResponseEntity<List<ConfiguracaoPaises>> configuracaoPaises(@RequestBody DtoPaises dados) {
		return ResponseEntity.ok().body(paisesService.checarPaises(dados));
	}
	
	@GetMapping("/configuracao/paises")
	public ResponseEntity<List<ConfiguracaoPaises>> buscaConfiguracaoPaises() {
		return ResponseEntity.ok().body(paisesService.listarPaises());
	}
	
	@GetMapping("/configuracao/email")
	public ResponseEntity<List<ConfiguracaoEmail>> buscaConfiguracaoEmails() {
		return ResponseEntity.ok().body(emailService.listarEmails());
	}
	
	@PutMapping("/configuracao/email")
	public void configuracaoEmails(@RequestBody List<DtoEmails> dados) {
		emailService.atualiza(dados);
	}
	
}
