package br.com.techgold.security.repository.microsoft;

import br.com.techgold.security.model.microsoft.M365ClienteConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface M365ClienteConfigRepository extends JpaRepository<M365ClienteConfig, Long> {

    List<M365ClienteConfig> findByAtivoTrue();

    List<M365ClienteConfig> findByAtivoTrueAndMonitorCaixaTrue();

    @Query("SELECT c FROM M365ClienteConfig c WHERE c.cliente.id = :clienteId ORDER BY c.nome")
    List<M365ClienteConfig> findByClienteId(Long clienteId);
}
