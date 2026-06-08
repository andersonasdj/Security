package br.com.techgold.security.repository.campanhaPhishing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.techgold.security.model.campanhaPhishing.PhishingConfigSmtp;

public interface PhishingConfigSmtpRepository extends JpaRepository<PhishingConfigSmtp, Long> {

    List<PhishingConfigSmtp> findByAtivoTrue();

    @Query("SELECT c FROM PhishingConfigSmtp c WHERE c.cliente.id = :clienteId ORDER BY c.nome")
    List<PhishingConfigSmtp> findByClienteId(Long clienteId);
}
