package br.com.techgold.security.model.campanhaPhishing;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_phishing_pagina_educacional_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhishingPaginaEducacionalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(name = "nome_empresa", length = 100)
    private String nomeEmpresa;

    /* ── Hero ── */
    @Column(name = "hero_badge_text", length = 150)
    private String heroBadgeText;

    @Column(name = "hero_titulo", length = 300)
    private String heroTitulo;

    @Column(name = "hero_titulo_destaque", length = 150)
    private String heroTituloDestaque;

    @Column(name = "hero_subtitulo", length = 500)
    private String heroSubtitulo;

    @Column(name = "hero_texto_seguranca", length = 250)
    private String heroTextoSeguranca;

    /* ── Explicação ── */
    @Column(name = "texto_explicacao", columnDefinition = "TEXT")
    private String textoExplicacao;

    /* ── Rodapé ── */
    @Column(name = "texto_footer", length = 300)
    private String textoFooter;

    /* ── Estatísticas ── */
    @Column(name = "stat1_valor", length = 30)
    private String stat1Valor;

    @Column(name = "stat1_label", length = 150)
    private String stat1Label;

    @Column(name = "stat1_cor", length = 20)
    private String stat1Cor;

    @Column(name = "stat2_valor", length = 30)
    private String stat2Valor;

    @Column(name = "stat2_label", length = 150)
    private String stat2Label;

    @Column(name = "stat2_cor", length = 20)
    private String stat2Cor;

    @Column(name = "stat3_valor", length = 30)
    private String stat3Valor;

    @Column(name = "stat3_label", length = 150)
    private String stat3Label;

    @Column(name = "stat3_cor", length = 20)
    private String stat3Cor;

    /* ── Dicas ── */
    @Column(name = "tip1_titulo", length = 100)
    private String tip1Titulo;

    @Column(name = "tip1_texto", length = 350)
    private String tip1Texto;

    @Column(name = "tip2_titulo", length = 100)
    private String tip2Titulo;

    @Column(name = "tip2_texto", length = 350)
    private String tip2Texto;

    @Column(name = "tip3_titulo", length = 100)
    private String tip3Titulo;

    @Column(name = "tip3_texto", length = 350)
    private String tip3Texto;

    @Column(name = "tip4_titulo", length = 100)
    private String tip4Titulo;

    @Column(name = "tip4_texto", length = 350)
    private String tip4Texto;

    /* ── Passos de ação ── */
    @Column(name = "acao_titulo", length = 200)
    private String acaoTitulo;

    @Column(name = "acao_passo1", length = 350)
    private String acaoPasso1;

    @Column(name = "acao_passo2", length = 350)
    private String acaoPasso2;

    @Column(name = "acao_passo3", length = 350)
    private String acaoPasso3;

    @Column(name = "acao_passo4", length = 350)
    private String acaoPasso4;

    /* ── Cores ── */
    @Column(name = "cor_destaque", length = 20)
    private String corDestaque;

    @Column(name = "cor_hero1", length = 20)
    private String corHero1;

    @Column(name = "cor_hero2", length = 20)
    private String corHero2;

    /* ── Visibilidade das seções ── */
    @Column(name = "exibir_stats")
    private boolean exibirStats = true;

    @Column(name = "exibir_explicacao")
    private boolean exibirExplicacao = true;

    @Column(name = "exibir_tips")
    private boolean exibirTips = true;

    @Column(name = "exibir_acao")
    private boolean exibirAcao = true;

    private boolean ativo;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now().withNano(0);
    }
}
