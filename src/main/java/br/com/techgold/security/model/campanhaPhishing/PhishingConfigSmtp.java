package br.com.techgold.security.model.campanhaPhishing;

import br.com.techgold.security.model.Cliente;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_phishing_config_smtp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhishingConfigSmtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(length = 150, nullable = false)
    private String host;

    @Column(nullable = false)
    private int porta;

    @Column(length = 150, nullable = false)
    private String usuario;

    @Column(length = 250, nullable = false)
    private String senha;

    @Column(name = "remetente_nome", length = 100, nullable = false)
    private String remetenteNome;

    @Column(name = "remetente_email", length = 150, nullable = false)
    private String remetenteEmail;

    @Column(name = "usar_tls")
    private boolean usarTls;

    private boolean ativo;

}
