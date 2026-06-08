package br.com.techgold.security.model.campanhaPhishing;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_phishing_modelo_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModeloEmailPhishing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String nome;

    @Column(length = 250, nullable = false)
    private String assunto;

    @Lob
    @Column(name = "corpo_html", columnDefinition = "LONGTEXT", nullable = false)
    private String corpoHtml;

    @Column(length = 100)
    private String categoria;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    private boolean ativo;

}
