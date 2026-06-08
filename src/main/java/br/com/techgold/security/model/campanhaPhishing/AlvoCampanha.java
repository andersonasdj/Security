package br.com.techgold.security.model.campanhaPhishing;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_phishing_alvo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlvoCampanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campanha_id", nullable = false)
    private CampanhaPhishing campanha;

    @Column(length = 150, nullable = false)
    private String nome;

    @Column(length = 150, nullable = false)
    private String email;

    @Column(length = 36, nullable = false, unique = true)
    private String token;

    private boolean enviado;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    private boolean clicou;

    @Column(name = "data_clique")
    private LocalDateTime dataClique;

    @Column(length = 60)
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

}
