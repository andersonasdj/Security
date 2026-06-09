package br.com.techgold.security.dto;

public record DtoFuncionarioHome(
		String nomeFuncionario,
		Long id,
		long totalClientes,
		long totalFuncionarios,
		long totalCampanhas,
		long totalAlvos,
		long totalEnviados,
		long totalClicados
) {
}
