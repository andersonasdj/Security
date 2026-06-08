package br.com.techgold.security.services.microsoft;

import br.com.techgold.security.model.microsoft.M365ClienteConfig;
import br.com.techgold.security.repository.microsoft.M365ClienteConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class M365ConfigService {

    @Autowired
    private M365ClienteConfigRepository repository;

    public List<M365ClienteConfig> listarTodos() {
        return repository.findAll();
    }

    public List<M365ClienteConfig> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public List<M365ClienteConfig> listarAtivosComMonitoramento() {
        return repository.findByAtivoTrueAndMonitorCaixaTrue();
    }

    public Optional<M365ClienteConfig> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<M365ClienteConfig> buscarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public M365ClienteConfig salvar(M365ClienteConfig config) {
        return repository.save(config);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }
}
