package br.com.techgold.security.dto;

import java.time.LocalDateTime;

public record DtoModeloEmailPhishing(
    Long id,
    String nome,
    String assunto,
    String corpoHtml,
    String categoria,
    LocalDateTime dataCriacao,
    boolean ativo
) {}
