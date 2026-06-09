package br.com.techgold.security.repository.campanhaPhishing;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.techgold.security.model.campanhaPhishing.PhishingPaginaEducacionalConfig;

public interface PhishingPaginaEducacionalConfigRepository extends JpaRepository<PhishingPaginaEducacionalConfig, Long> {

    Optional<PhishingPaginaEducacionalConfig> findFirstByAtivoTrue();
}
