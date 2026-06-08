package br.com.techgold.security.model.campanhaPhishing;

import java.time.LocalDateTime;

import br.com.techgold.security.model.Cliente;
import br.com.techgold.security.model.campanhaPhishing.enums.StatusCampanha;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_phishing_campanha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampanhaPhishing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_email_id", nullable = false)
    private ModeloEmailPhishing modeloEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_smtp_id", nullable = false)
    private PhishingConfigSmtp configSmtp;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusCampanha status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "criado_por", length = 150)
    private String criadoPor;

}
