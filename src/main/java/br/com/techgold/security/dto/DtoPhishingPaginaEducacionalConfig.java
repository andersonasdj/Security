package br.com.techgold.security.dto;

public record DtoPhishingPaginaEducacionalConfig(
    Long id,
    String nome,
    String nomeEmpresa,
    String heroBadgeText,
    String heroTitulo,
    String heroTituloDestaque,
    String heroSubtitulo,
    String heroTextoSeguranca,
    String textoExplicacao,
    String textoFooter,
    String stat1Valor, String stat1Label, String stat1Cor,
    String stat2Valor, String stat2Label, String stat2Cor,
    String stat3Valor, String stat3Label, String stat3Cor,
    String tip1Titulo, String tip1Texto,
    String tip2Titulo, String tip2Texto,
    String tip3Titulo, String tip3Texto,
    String tip4Titulo, String tip4Texto,
    String acaoTitulo,
    String acaoPasso1, String acaoPasso2, String acaoPasso3, String acaoPasso4,
    String corDestaque, String corHero1, String corHero2,
    boolean exibirStats,
    boolean exibirExplicacao,
    boolean exibirTips,
    boolean exibirAcao,
    boolean ativo
) {}
