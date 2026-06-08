package br.com.techgold.security.services.campanhaPhishing;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.techgold.security.model.campanhaPhishing.AlvoCampanha;
import br.com.techgold.security.repository.campanhaPhishing.AlvoCampanhaRepository;

@Service
public class RastreamentoPhishingService {

    @Autowired
    private AlvoCampanhaRepository alvoRepository;

    public Optional<AlvoCampanha> registrarClique(String token, String ip, String userAgent) {
        Optional<AlvoCampanha> alvoOpt = alvoRepository.findByToken(token);

        alvoOpt.ifPresent(alvo -> {
            if (!alvo.isClicou()) {
                alvo.setClicou(true);
                alvo.setDataClique(LocalDateTime.now().withNano(0));
                alvo.setIp(ip);
                alvo.setUserAgent(userAgent);
                alvoRepository.save(alvo);
            }
        });

        return alvoOpt;
    }
}
