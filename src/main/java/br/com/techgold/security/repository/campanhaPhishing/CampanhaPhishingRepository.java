package br.com.techgold.security.repository.campanhaPhishing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.techgold.security.model.campanhaPhishing.CampanhaPhishing;
import br.com.techgold.security.orm.campanhaPhishing.CampanhaResumoProjecao;

public interface CampanhaPhishingRepository extends JpaRepository<CampanhaPhishing, Long> {

    @Query("SELECT c FROM CampanhaPhishing c WHERE c.cliente.id = :clienteId ORDER BY c.dataCriacao DESC")
    List<CampanhaPhishing> findByClienteId(Long clienteId);

    List<CampanhaPhishing> findAllByOrderByDataCriacaoDesc();

    @Query("SELECT c.id as id, c.nome as nome, c.status as status, "
         + "(SELECT COUNT(a) FROM AlvoCampanha a WHERE a.campanha = c) as totalAlvos, "
         + "(SELECT COUNT(a) FROM AlvoCampanha a WHERE a.campanha = c AND a.enviado = true) as enviados, "
         + "(SELECT COUNT(a) FROM AlvoCampanha a WHERE a.campanha = c AND a.clicou = true) as clicados "
         + "FROM CampanhaPhishing c ORDER BY c.dataCriacao DESC")
    List<CampanhaResumoProjecao> buscarResumo();

    @Query("SELECT c.id as id, c.nome as nome, c.status as status, "
         + "(SELECT COUNT(a) FROM AlvoCampanha a WHERE a.campanha = c) as totalAlvos, "
         + "(SELECT COUNT(a) FROM AlvoCampanha a WHERE a.campanha = c AND a.enviado = true) as enviados, "
         + "(SELECT COUNT(a) FROM AlvoCampanha a WHERE a.campanha = c AND a.clicou = true) as clicados "
         + "FROM CampanhaPhishing c WHERE c.id = :campanhaId")
    CampanhaResumoProjecao buscarResumoPorId(Long campanhaId);
}
