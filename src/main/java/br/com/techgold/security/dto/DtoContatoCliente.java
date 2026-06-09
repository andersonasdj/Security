package br.com.techgold.security.dto;

public record DtoContatoCliente(
        Long id,
        String nome,
        String email,
        Long clienteId,
        String nomeCliente,
        boolean ativo,
        String dataCriacao
) {}
