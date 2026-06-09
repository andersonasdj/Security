package br.com.techgold.security.services.campanhaPhishing;

import java.time.LocalDateTime;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.techgold.security.model.campanhaPhishing.AlvoCampanha;
import br.com.techgold.security.model.campanhaPhishing.CampanhaPhishing;
import br.com.techgold.security.model.campanhaPhishing.ModeloEmailPhishing;
import br.com.techgold.security.model.campanhaPhishing.PhishingConfigSmtp;
import br.com.techgold.security.model.campanhaPhishing.enums.StatusCampanha;
import br.com.techgold.security.repository.campanhaPhishing.AlvoCampanhaRepository;
import br.com.techgold.security.repository.campanhaPhishing.CampanhaPhishingRepository;
import jakarta.mail.internet.MimeMessage;

@Service
public class EnvioPhishingService {

    private static final String PLACEHOLDER_LINK = "{{LINK_RASTREIO}}";

    @Value("${sistech.phishing.base-url}")
    private String baseUrl;

    @Autowired
    private CampanhaPhishingRepository campanhaRepository;

    @Autowired
    private AlvoCampanhaRepository alvoRepository;

    @Async
    @Transactional
    public void disparar(Long campanhaId) {
        CampanhaPhishing campanha = campanhaRepository.findById(campanhaId).orElseThrow();
        ModeloEmailPhishing modelo = campanha.getModeloEmail();
        PhishingConfigSmtp config = campanha.getConfigSmtp();
        JavaMailSenderImpl mailSender = construirMailSender(config);

        campanha.setStatus(StatusCampanha.ENVIANDO);
        campanha.setDataInicio(LocalDateTime.now().withNano(0));
        campanhaRepository.save(campanha);

        int tamanheLote = config.getTamanheLote() > 0 ? config.getTamanheLote() : 10;
        int intervaloSegundos = config.getIntervaloSegundos();
        int enviadosNoLote = 0;

        for (AlvoCampanha alvo : alvoRepository.findByCampanhaId(campanha.getId())) {
            if (alvo.isEnviado()) continue;
            try {
                String linkRastreio = baseUrl + "/phishing/click/" + alvo.getToken();
                String corpo = modelo.getCorpoHtml().replace(PLACEHOLDER_LINK, linkRastreio);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(config.getRemetenteEmail(), config.getRemetenteNome());
                helper.setTo(alvo.getEmail());
                helper.setSubject(modelo.getAssunto());
                helper.setText(corpo, true);
                mailSender.send(message);

                alvo.setEnviado(true);
                alvo.setDataEnvio(LocalDateTime.now().withNano(0));
                alvoRepository.save(alvo);
                enviadosNoLote++;

                if (intervaloSegundos > 0 && enviadosNoLote >= tamanheLote) {
                    enviadosNoLote = 0;
                    try { Thread.sleep(intervaloSegundos * 1000L); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                }
            } catch (Exception e) {
                System.out.println("[Phishing] Erro ao enviar para " + alvo.getEmail() + ": " + e.getMessage());
            }
        }

        campanha.setStatus(StatusCampanha.CONCLUIDA);
        campanha.setDataConclusao(LocalDateTime.now().withNano(0));
        campanhaRepository.save(campanha);
    }

    private JavaMailSenderImpl construirMailSender(PhishingConfigSmtp config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPorta());
        mailSender.setUsername(config.getUsuario());
        mailSender.setPassword(config.getSenha());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(config.isUsarTls()));

        return mailSender;
    }
}
