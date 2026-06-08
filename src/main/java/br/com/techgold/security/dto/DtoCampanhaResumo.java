package br.com.techgold.security.dto;

public record DtoCampanhaResumo(
    Long id,
    String nome,
    String status,
    long totalAlvos,
    long enviados,
    long clicados,
    double taxaClique
) {}
