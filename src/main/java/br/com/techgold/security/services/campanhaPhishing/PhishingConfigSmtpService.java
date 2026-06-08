package br.com.techgold.security.services.campanhaPhishing;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.techgold.security.model.campanhaPhishing.PhishingConfigSmtp;
import br.com.techgold.security.repository.campanhaPhishing.PhishingConfigSmtpRepository;

@Service
public class PhishingConfigSmtpService {

    @Autowired
    private PhishingConfigSmtpRepository repository;

    public List<PhishingConfigSmtp> listarTodos() {
        return repository.findAll();
    }

    public List<PhishingConfigSmtp> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public List<PhishingConfigSmtp> buscarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public Optional<PhishingConfigSmtp> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public PhishingConfigSmtp salvar(PhishingConfigSmtp config) {
        return repository.save(config);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }
}
