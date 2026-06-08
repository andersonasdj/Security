package br.com.techgold.security.repository.campanhaPhishing;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.techgold.security.model.campanhaPhishing.AlvoCampanha;
import br.com.techgold.security.orm.campanhaPhishing.AlvoCliqueProjecao;

public interface AlvoCampanhaRepository extends JpaRepository<AlvoCampanha, Long> {

    Optional<AlvoCampanha> findByToken(String token);

    List<AlvoCampanha> findByCampanhaId(Long campanhaId);

    long countByCampanhaId(Long campanhaId);

    long countByCampanhaIdAndEnviadoTrue(Long campanhaId);

    long countByCampanhaIdAndClicouTrue(Long campanhaId);

    @Query("SELECT a.nome as nome, a.email as email, a.enviado as enviado, a.clicou as clicou, "
         + "a.dataEnvio as dataEnvio, a.dataClique as dataClique "
         + "FROM AlvoCampanha a WHERE a.campanha.id = :campanhaId ORDER BY a.nome")
    List<AlvoCliqueProjecao> buscarCliquesPorCampanha(Long campanhaId);
}
