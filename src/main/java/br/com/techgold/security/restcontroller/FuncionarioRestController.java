package br.com.techgold.security.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.security.dto.DtoCadastroFuncionario;
import br.com.techgold.security.dto.DtoFuncionarioAdvancedEdit;
import br.com.techgold.security.dto.DtoFuncionarioAdvancedList;

import br.com.techgold.security.dto.DtoFuncionarioEdit;
import br.com.techgold.security.dto.DtoFuncionarioHome;
import br.com.techgold.security.dto.DtoFuncionarioNavbar;

import br.com.techgold.security.dto.DtoListarFuncionarios;
import br.com.techgold.security.dto.DtoSenha;
import br.com.techgold.security.model.Funcionario;
import br.com.techgold.security.orm.DtoFuncionarioEditSimplificado;

import br.com.techgold.security.services.FuncionarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("funcionarios")
public class FuncionarioRestController {
	
	@Autowired FuncionarioService service;
	
	@PreAuthorize("hasRole('ROLE_SADMIN')")
	@PostMapping
	public void cadastrar(@RequestBody @Valid DtoCadastroFuncionario dados ) {
		service.salvar(dados);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping //RETORNA UMA DTO COM A LISTA DE TODOS OS FUNCIONARIOS
	public ResponseEntity<List<DtoListarFuncionarios>> listar(){
		return ResponseEntity.ok().body(service.listar());
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{id}") //RESTORNA UMA DTO DE UM FUNCIONARIO POR ID
	public ResponseEntity<DtoFuncionarioEdit> editar(@PathVariable Long id ) {
		return ResponseEntity.ok().body(service.editar(id));
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/perfil") //RETORNA DADOS DO FUNCIONARIO PARA PROPRIA EDICAO (PERFIL)
	public ResponseEntity<DtoFuncionarioEditSimplificado> perfil(){
		Funcionario funcionario = service.buscaPorNome(((Funcionario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeFuncionario());
		return ResponseEntity.ok().body(service.buscaDadosFuncionario(funcionario.getId()));
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/home") //RETORNA UMA DTO COM OS DADOS PARA A HOME PAGE
	public ResponseEntity<DtoFuncionarioHome> funcionarioHome() {
	
		Funcionario funcionario = service.buscaPorNome(((Funcionario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeFuncionario());

		return ResponseEntity.ok().body(
				new DtoFuncionarioHome(
						funcionario.getNomeFuncionario(),
						funcionario.getId()
				));
	}	

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/nav")
	public ResponseEntity<DtoFuncionarioNavbar> funcionarioNavbar() {
		Funcionario funcionario = service.buscaPorNome(((Funcionario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeFuncionario());
		return ResponseEntity.ok(new DtoFuncionarioNavbar(funcionario.getNomeFuncionario()));
	}
	
	@PreAuthorize("hasRole('ROLE_SADMIN')")
	@PutMapping //ATUALIZA UM FUNCIONARIO
	public ResponseEntity<DtoListarFuncionarios> atualizar(@RequestBody DtoFuncionarioEdit dados) {
		return ResponseEntity.ok().body(service.atualizarFuncionario(dados));
	}
	
	@PreAuthorize("hasRole('ROLE_SADMIN')")
	@PutMapping("/senha") //ATUALIZA A SENHA PARA QUALQUER USUARIO
	public boolean atualizar(@RequestBody DtoSenha dados) {
		return service.atualizarSenha(dados);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@PutMapping("/senha/pessoal") //ATUALIZA A SENHA SOMENTE DO PROPRIO USUARIO
	public boolean atualizarSenhaPessoal(@RequestBody DtoSenha dados) {
		
		Funcionario funcionario = service.buscaPorNome(((Funcionario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeFuncionario());
		if(funcionario.getId() == dados.id()) {
			return service.atualizarSenha(dados);
		}else {
			return false;
		}
	}
	
	@PreAuthorize("hasRole('ROLE_SADMIN')")
	@DeleteMapping("/{id}")
	public boolean deletar(@PathVariable Long id ) {
		return service.deletar(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/avancado/{id}") //RESTORNA UMA DTO DE UM FUNCIONARIO POR ID
	public ResponseEntity<DtoFuncionarioAdvancedList> edicaoAvancada(@PathVariable Long id ) {
		return ResponseEntity.ok().body(service.advancedEdit(id));
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/avancado") //RESTORNA UMA DTO DE UM FUNCIONARIO POR ID
	public ResponseEntity<DtoFuncionarioAdvancedEdit> salvarEdicaoAvancada(@RequestBody DtoFuncionarioAdvancedEdit dados ) {
		return ResponseEntity.ok().body(service.atualizarFuncionarioAdvanced(dados));
	}
	
}
