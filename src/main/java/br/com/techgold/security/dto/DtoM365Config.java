package br.com.techgold.security.dto;

public record DtoM365Config(
    Long id,
    Long clienteId,
    String nomeCliente,
    String nome,
    String tenantId,
    String clientId,
    boolean ativo,
    boolean monitorCaixa
) {}