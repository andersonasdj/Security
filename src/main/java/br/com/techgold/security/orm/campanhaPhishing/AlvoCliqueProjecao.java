package br.com.techgold.security.orm.campanhaPhishing;

import java.time.LocalDateTime;

public interface AlvoCliqueProjecao {
	String getNome();
	String getEmail();
	boolean isEnviado();
	boolean isClicou();
	LocalDateTime getDataEnvio();
	LocalDateTime getDataClique();
}
