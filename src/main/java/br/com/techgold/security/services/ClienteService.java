package br.com.techgold.security.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.techgold.security.dto.DtoAtualizarCliente;
import br.com.techgold.security.dto.DtoCadastroCliente;
import br.com.techgold.security.dto.DtoClienteList;
import br.com.techgold.security.model.Cliente;
import br.com.techgold.security.repository.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired
	ClienteRepository repository;
	
	public Cliente buscaClientePorToken(String token) {
		return repository.findByToken(token);
	}
	
	public List<DtoClienteList> listarAtivos() {
		return repository.listarClientes().stream().map(DtoClienteList::new).toList();
	}
	
	public Page<DtoClienteList> listarClientePorPalavra(Pageable page, String conteudo) {
		return repository.listarClientesPorPalavra(page, conteudo).map(DtoClienteList::new);
	}

	public List<String> listarNomesClienteAtivos() {
		return repository.listarNomesClienteAtivos();
	}
	
	public List<String> listarIdClienteAtivos() {
		return repository.listarIdClienteAtivos();
	}
	
	public Page<DtoClienteList> listarTodos(Pageable page) {
		return repository.findAll(page).map(DtoClienteList::new);
	}
	
	public List<String> listarBairrosClientes(){
		List<String> listaDeBairros = new ArrayList<>();
		List<String> listaCompletaBairros = repository.listarBairrosClientes();

		listaCompletaBairros.forEach( b -> {
			if(b != null && !b.isBlank() && !listaDeBairros.contains(b)) {
				listaDeBairros.add(b);
			}
		});
		return listaDeBairros;
	}

	public void cadastrarNovoCliente(DtoCadastroCliente dados) {
		repository.save(new Cliente(dados));
	}

	public Cliente atualizarCliente(DtoAtualizarCliente dados) {
		return repository.save(new Cliente(dados));
	}
	
	public Cliente buscaClientePorNome(Long dados) {
		return  repository.getReferenceById(dados);
	}
	
	public boolean verificaSeVip(Long id) {
		return repository.verificaSeVip(id);
	}
	
	public boolean verificaSeRedFlag(Long id) {
		return repository.verificaSeRedFlag(id);
	}
	
}
