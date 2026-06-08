package br.com.techgold.security.dto;

import java.math.BigDecimal;

import br.com.techgold.security.model.Funcionario;
import br.com.techgold.security.model.UserRole;

public record DtoFuncionarioAdvancedEdit(
		Long id,
		UserRole role,
		BigDecimal valorHora
		) {
	
	public DtoFuncionarioAdvancedEdit(Funcionario f) {
		this(
				f.getId(), 
				f.getRole(),
				f.getValorHora()
				);
	}

}
