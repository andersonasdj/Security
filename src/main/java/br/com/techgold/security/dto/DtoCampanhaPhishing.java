package br.com.techgold.security.dto;

import java.time.LocalDateTime;

public record DtoCampanhaPhishing(
    Long id,
    String nome,
    String descricao,
    Long clienteId,
    String nomeCliente,
    Long modeloEmailId,
    String nomeModeloEmail,
    Long configSmtpId,
    String nomeConfigSmtp,
    String status,
    LocalDateTime dataCriacao,
    LocalDateTime dataInicio,
    LocalDateTime dataConclusao,
    String criadoPor,
    long totalAlvos
) {}
