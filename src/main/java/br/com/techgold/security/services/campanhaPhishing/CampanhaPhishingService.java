package br.com.techgold.security.services.campanhaPhishing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.techgold.security.model.campanhaPhishing.AlvoCampanha;
import br.com.techgold.security.model.campanhaPhishing.CampanhaPhishing;
import br.com.techgold.security.model.campanhaPhishing.enums.StatusCampanha;
import br.com.techgold.security.orm.campanhaPhishing.AlvoCliqueProjecao;
import br.com.techgold.security.orm.campanhaPhishing.CampanhaResumoProjecao;
import br.com.techgold.security.repository.campanhaPhishing.AlvoCampanhaRepository;
import br.com.techgold.security.repository.campanhaPhishing.CampanhaPhishingRepository;

@Service
public class CampanhaPhishingService {

    @Autowired
    private CampanhaPhishingRepository repository;

    @Autowired
    private AlvoCampanhaRepository alvoRepository;

    public List<CampanhaPhishing> listarTodas() {
        return repository.findAllByOrderByDataCriacaoDesc();
    }

    public List<CampanhaPhishing> buscarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public Optional<CampanhaPhishing> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<CampanhaResumoProjecao> buscarResumo() {
        return repository.buscarResumo();
    }

    public CampanhaResumoProjecao buscarResumoPorId(Long id) {
        return repository.buscarResumoPorId(id);
    }

    public List<AlvoCliqueProjecao> buscarCliquesPorCampanha(Long campanhaId) {
        return alvoRepository.buscarCliquesPorCampanha(campanhaId);
    }

    public List<AlvoCampanha> buscarAlvosPorCampanha(Long campanhaId) {
        return alvoRepository.findByCampanhaId(campanhaId);
    }

    public CampanhaPhishing salvar(CampanhaPhishing campanha) {
        if (campanha.getId() == null) {
            campanha.setStatus(StatusCampanha.RASCUNHO);
            campanha.setDataCriacao(LocalDateTime.now().withNano(0));
        }
        return repository.save(campanha);
    }

    public void deletar(Long id) {
        List<AlvoCampanha> alvos = alvoRepository.findByCampanhaId(id);
        if (!alvos.isEmpty()) alvoRepository.deleteAll(alvos);
        repository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<AlvoCampanha> definirAlvos(CampanhaPhishing campanha, List<String[]> alvos) {
        alvoRepository.deleteAll(alvoRepository.findByCampanhaId(campanha.getId()));

        List<AlvoCampanha> novosAlvos = alvos.stream().map(dados -> {
            AlvoCampanha alvo = new AlvoCampanha();
            alvo.setCampanha(campanha);
            alvo.setNome(dados[0]);
            alvo.setEmail(dados[1]);
            alvo.setToken(UUID.randomUUID().toString());
            alvo.setEnviado(false);
            alvo.setClicou(false);
            return alvo;
        }).toList();

        return alvoRepository.saveAll(novosAlvos);
    }
}
