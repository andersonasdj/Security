package br.com.techgold.security.services.campanhaPhishing;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.techgold.security.model.campanhaPhishing.PhishingPaginaEducacionalConfig;
import br.com.techgold.security.repository.campanhaPhishing.PhishingPaginaEducacionalConfigRepository;

@Service
public class PhishingPaginaEducacionalConfigService {

    @Autowired
    private PhishingPaginaEducacionalConfigRepository repository;

    public List<PhishingPaginaEducacionalConfig> listarTodos() {
        return repository.findAll();
    }

    public Optional<PhishingPaginaEducacionalConfig> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<PhishingPaginaEducacionalConfig> buscarAtiva() {
        return repository.findFirstByAtivoTrue();
    }

    public PhishingPaginaEducacionalConfig salvar(PhishingPaginaEducacionalConfig config) {
        return repository.save(config);
    }

    @Transactional
    public void ativar(Long id) {
        repository.findAll().forEach(c -> {
            c.setAtivo(false);
            repository.save(c);
        });
        repository.findById(id).ifPresent(c -> {
            c.setAtivo(true);
            repository.save(c);
        });
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }
}
