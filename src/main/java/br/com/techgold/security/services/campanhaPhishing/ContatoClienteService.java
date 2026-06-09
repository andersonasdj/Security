package br.com.techgold.security.services.campanhaPhishing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.techgold.security.dto.DtoContatoCliente;
import br.com.techgold.security.model.Cliente;
import br.com.techgold.security.model.campanhaPhishing.ContatoCliente;
import br.com.techgold.security.repository.ClienteRepository;
import br.com.techgold.security.repository.campanhaPhishing.ContatoClienteRepository;

@Service
public class ContatoClienteService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Autowired private ContatoClienteRepository repository;
    @Autowired private ClienteRepository clienteRepository;

    public List<DtoContatoCliente> listarPorCliente(Long clienteId) {
        return repository.findByClienteIdOrderByNomeAsc(clienteId)
                .stream().map(this::toDto).toList();
    }

    public Optional<ContatoCliente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContatoCliente salvar(Long clienteId, String nome, String email) {
        Cliente cliente = clienteRepository.getReferenceById(clienteId);
        ContatoCliente c = new ContatoCliente();
        c.setCliente(cliente);
        c.setNome(nome.strip());
        c.setEmail(email.strip().toLowerCase());
        c.setAtivo(true);
        return repository.save(c);
    }

    @Transactional
    public Optional<ContatoCliente> atualizar(Long id, String nome, String email, boolean ativo) {
        return repository.findById(id).map(c -> {
            c.setNome(nome.strip());
            c.setEmail(email.strip().toLowerCase());
            c.setAtivo(ativo);
            return repository.save(c);
        });
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    /** Importa uma lista de linhas no formato "nome,email" ou "nome;email".
     *  Ignora cabeçalho, linhas vazias e duplicatas (mesmo e-mail no mesmo cliente).
     *  Retorna mapa com contagens: importados, duplicados, invalidos, erros. */
    @Transactional
    public Map<String, Object> importar(Long clienteId, List<String> linhas) {
        Cliente cliente = clienteRepository.getReferenceById(clienteId);
        int importados = 0, duplicados = 0, invalidos = 0;
        List<String> erros = new ArrayList<>();

        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i).strip();
            if (linha.isBlank()) continue;

            String[] partes = linha.contains(";") ? linha.split(";", 2) : linha.split(",", 2);
            if (partes.length < 2) {
                invalidos++;
                erros.add("Linha " + (i + 1) + ": formato inválido — \"" + linha + "\"");
                continue;
            }

            String nome  = partes[0].strip();
            String email = partes[1].strip().toLowerCase();

            // pular linha de cabeçalho
            if (nome.equalsIgnoreCase("nome") && email.equalsIgnoreCase("email")) continue;
            if (nome.isBlank() || email.isBlank()) {
                invalidos++;
                erros.add("Linha " + (i + 1) + ": nome ou e-mail vazio");
                continue;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                invalidos++;
                erros.add("Linha " + (i + 1) + ": e-mail inválido — \"" + email + "\"");
                continue;
            }
            if (repository.existsByEmailAndClienteId(email, clienteId)) {
                duplicados++;
                continue;
            }

            ContatoCliente c = new ContatoCliente();
            c.setCliente(cliente);
            c.setNome(nome);
            c.setEmail(email);
            c.setAtivo(true);
            repository.save(c);
            importados++;
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("importados", importados);
        resultado.put("duplicados", duplicados);
        resultado.put("invalidos",  invalidos);
        resultado.put("total",      linhas.size());
        resultado.put("erros",      erros);
        return resultado;
    }

    private DtoContatoCliente toDto(ContatoCliente c) {
        String data = c.getDataCriacao() != null ? c.getDataCriacao().toString() : null;
        return new DtoContatoCliente(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getCliente().getId(),
                c.getCliente().getNomeCliente(),
                c.isAtivo(),
                data
        );
    }
}
