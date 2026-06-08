package br.com.techgold.security.services.campanhaPhishing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.techgold.security.model.campanhaPhishing.ModeloEmailPhishing;
import br.com.techgold.security.repository.campanhaPhishing.ModeloEmailPhishingRepository;

@Service
public class ModeloEmailPhishingService {

    @Autowired
    private ModeloEmailPhishingRepository repository;

    public List<ModeloEmailPhishing> listarTodos() {
        return repository.findAll();
    }

    public List<ModeloEmailPhishing> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public Optional<ModeloEmailPhishing> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ModeloEmailPhishing salvar(ModeloEmailPhishing modelo) {
        if (modelo.getId() == null) {
            modelo.setDataCriacao(LocalDateTime.now().withNano(0));
        }
        return repository.save(modelo);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }
}
