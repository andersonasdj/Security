package br.com.techgold.security.dto;

import java.math.BigDecimal;

import br.com.techgold.security.model.Funcionario;

public record DtoListarCustoFuncionarios(
		Long id,
		BigDecimal valorHora) {
	
	public DtoListarCustoFuncionarios(Funcionario f) {
		this(
				f.getId(), 
				f.getValorHora()
				);
	}

}
