package br.com.techgold.security.orm.microsoft;

import java.util.List;

public record M365CaixaPostalView(
    String userPrincipalName,
    String displayName,
    long totalItemCount,
    long storageUsedBytes,
    long storageLimitBytes,
    boolean deleted,
    String mailboxType,
    String department,
    String createdDate,
    String lastActivityDate,
    boolean accountEnabled,
    List<String> licencaSkuIds,
    String ultimaTrocaSenha
) {}
