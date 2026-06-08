package br.com.techgold.security.dto;

import java.util.List;

public record DtoM365CaixaPostal(
    String userPrincipalName,
    String displayName,
    long totalItemCount,
    long storageUsedBytes,
    long storageLimitBytes,
    boolean deleted,
    double percentualUso,
    String mailboxType,
    String department,
    String createdDate,
    String lastActivityDate,
    boolean accountEnabled,
    List<String> licencaSkuIds,
    String ultimaTrocaSenha
) {}
