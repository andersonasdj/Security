package br.com.techgold.security.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.techgold.security.model.Funcionario;

public record DtoFuncionarioEdit(
		Long id,
		String nomeFuncionario,
		String username,
		Boolean ativo,
		Boolean mfa,
		Boolean ausente,
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm")
		LocalDateTime dataAtualizacao,
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm")
		LocalDateTime dataAtualizacaoSenha,
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm")
		LocalDateTime dataUltimoLogin,
		Boolean trocaSenha,
		String email
		) {
	
	public DtoFuncionarioEdit(Funcionario f) {
		this(
				f.getId(), 
				f.getNomeFuncionario(), 
				f.getUsername(), 
				f.getAtivo(),
				f.getMfa(),
				(f.getAusente()) != null ? f.getAusente(): false,
				f.getDataAtualizacao(),
				f.getDataAtualizacaoSenha(),
				f.getDataUltimoLogin(),
				(f.getTrocaSenha()) != null? f.getTrocaSenha(): false,
				f.getEmail()
				);
	}

}
