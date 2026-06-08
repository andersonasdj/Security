package br.com.techgold.security.repository.campanhaPhishing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.techgold.security.model.campanhaPhishing.ModeloEmailPhishing;

public interface ModeloEmailPhishingRepository extends JpaRepository<ModeloEmailPhishing, Long> {

    List<ModeloEmailPhishing> findByAtivoTrue();
}
