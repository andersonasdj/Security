package br.com.techgold.security.dto;

import br.com.techgold.security.model.enums.Agendamentos;

public record DtoEmails(
		Long id,
		Agendamentos agendamento,
		String email,
		boolean status
		) {

}
