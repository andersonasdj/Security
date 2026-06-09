package br.com.techgold.security.repository.campanhaPhishing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.techgold.security.model.campanhaPhishing.ContatoCliente;

public interface ContatoClienteRepository extends JpaRepository<ContatoCliente, Long> {

    List<ContatoCliente> findByClienteIdOrderByNomeAsc(Long clienteId);

    boolean existsByEmailAndClienteId(String email, Long clienteId);

    long countByClienteId(Long clienteId);
}
