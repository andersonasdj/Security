package br.com.techgold.security.model.microsoft;

import br.com.techgold.security.model.Cliente;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "m365_cliente_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class M365ClienteConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(name = "tenant_id", length = 150, nullable = false)
    private String tenantId;

    @Column(name = "client_id", length = 150, nullable = false)
    private String clientId;

    @Column(name = "client_secret", length = 250, nullable = false)
    private String clientSecret;

    private boolean ativo;

    private boolean monitorCaixa;

}
