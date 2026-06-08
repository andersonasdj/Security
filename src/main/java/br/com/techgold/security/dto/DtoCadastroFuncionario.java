package br.com.techgold.security.dto;

import br.com.techgold.security.model.UserRole;
import jakarta.validation.constraints.NotBlank;

public record DtoCadastroFuncionario(
		
		@NotBlank
		String nomeFuncionario,
		@NotBlank
		String username,
		@NotBlank
		String password,
		UserRole role) {

}
