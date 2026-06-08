package br.com.techgold.security.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.security.dto.DtoCadastroFuncionario;
import br.com.techgold.security.model.ConfiguracaoEmail;
import br.com.techgold.security.model.ConfiguracaoPaises;
import br.com.techgold.security.model.enums.Agendamentos;
import br.com.techgold.security.services.ConfiguracaoEmailService;
import br.com.techgold.security.services.ConfiguracaoPaisesService;
import br.com.techgold.security.services.FuncionarioService;

@RestController
@RequestMapping()
public class CreateConfigController {
	
	@Autowired
	private FuncionarioService service;
	
	@Autowired
	private ConfiguracaoPaisesService paisesService;
	
	@Autowired
	private ConfiguracaoEmailService emailService;
	

	@PostMapping("/create")
	public String register(@RequestBody DtoCadastroFuncionario dados ) {
		
		if(paisesService.existeConfig() == 0) {
			paisesService.salvar(new ConfiguracaoPaises("BR",true));
			paisesService.salvar(new ConfiguracaoPaises("US",true));
			paisesService.salvar(new ConfiguracaoPaises("PT",true));
			paisesService.salvar(new ConfiguracaoPaises("CA",true));
			paisesService.salvar(new ConfiguracaoPaises("FR",true));
			paisesService.salvar(new ConfiguracaoPaises("CL",true));
		}
		
		if(emailService.existeEmail() == 0) {
			emailService.cadastra(new ConfiguracaoEmail( " ", false, Agendamentos.AGENDAMENTO));
			emailService.cadastra(new ConfiguracaoEmail( " ", false, Agendamentos.ABERTURA));
		}
		
		if(service.existeFuncionarios() == 0) {
			service.salvar(dados);
			return "Usuário cadastrado com sucesso!";
		} else {
			return "Erro!";
		}
	}
	

}
