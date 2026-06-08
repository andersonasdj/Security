package br.com.techgold.security.orm.campanhaPhishing;

import br.com.techgold.security.model.campanhaPhishing.enums.StatusCampanha;

public interface CampanhaResumoProjecao {
	Long getId();
	String getNome();
	StatusCampanha getStatus();
	Long getTotalAlvos();
	Long getEnviados();
	Long getClicados();
}
