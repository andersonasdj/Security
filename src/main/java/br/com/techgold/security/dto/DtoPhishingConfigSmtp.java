package br.com.techgold.security.dto;

public record DtoPhishingConfigSmtp(
    Long id,
    Long clienteId,
    String nomeCliente,
    String nome,
    String host,
    int porta,
    String usuario,
    String remetenteNome,
    String remetenteEmail,
    boolean usarTls,
    boolean ativo,
    int tamanheLote,
    int intervaloSegundos
) {}
