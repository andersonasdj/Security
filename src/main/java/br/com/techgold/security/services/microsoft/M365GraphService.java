package br.com.techgold.security.services.microsoft;

import br.com.techgold.security.model.microsoft.M365ClienteConfig;
import br.com.techgold.security.orm.microsoft.M365CaixaPostalView;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class M365GraphService {

    private static final String GRAPH_REPORTS_URL =
            "https://graph.microsoft.com/v1.0/reports/getMailboxUsageDetail(period='D30')";

    private static final String GRAPH_USERS_URL =
            "https://graph.microsoft.com/v1.0/users?$select=userPrincipalName,displayName,accountEnabled,department,mail,userType,assignedLicenses,lastPasswordChangeDateTime&$top=999";

    private static final String GRAPH_BATCH_URL =
            "https://graph.microsoft.com/v1.0/$batch";

    private static final int BATCH_SIZE = 20;

    private final ObjectMapper mapper = new ObjectMapper();

    private record StorageData(
            long totalItemCount, long storageUsedBytes, long storageLimitBytes, boolean deleted,
            String createdDate, String lastActivityDate) {
        static StorageData vazia() { return new StorageData(0, 0, 0, false, "", ""); }
    }

    private record UserBasico(String upn, String displayName, boolean accountEnabled, String department, List<String> licencaSkuIds, String ultimaTrocaSenha) {}

    public List<M365CaixaPostalView> buscarCaixasPostais(M365ClienteConfig config) throws Exception {
        String token = obterToken(config);

        Map<String, StorageData> dadosStorage = parsearCSVParaStorageMap(chamarRelatorioGraph(token));
        List<UserBasico> usuarios             = buscarTodosUsuariosComMail(token);
        Map<String, String> tiposMailbox      = buscarTiposMailboxEmLote(token, usuarios);

        var result = new ArrayList<M365CaixaPostalView>();
        for (UserBasico u : usuarios) {
            String upnLower     = u.upn().toLowerCase();
            String mailboxType  = resolverTipo(tiposMailbox.getOrDefault(upnLower, ""), u.accountEnabled());
            StorageData storage = dadosStorage.getOrDefault(upnLower, StorageData.vazia());

            result.add(new M365CaixaPostalView(
                    u.upn(), u.displayName(),
                    storage.totalItemCount(), storage.storageUsedBytes(), storage.storageLimitBytes(),
                    storage.deleted(), mailboxType, u.department(),
                    storage.createdDate(), storage.lastActivityDate(), u.accountEnabled(), u.licencaSkuIds(),
                    u.ultimaTrocaSenha()));
        }
        return result;
    }

    public String buscarCSVBruto(M365ClienteConfig config) throws Exception {
        return chamarRelatorioGraph(obterToken(config));
    }

    // ── Token ─────────────────────────────────────────────────────────────────

    private String obterToken(M365ClienteConfig config) {
        var credential = new ClientSecretCredentialBuilder()
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .tenantId(config.getTenantId())
                .build();
        var token = credential.getToken(new TokenRequestContext()
                .addScopes("https://graph.microsoft.com/.default")).block();
        if (token == null) throw new RuntimeException("Falha ao obter token Microsoft Graph");
        return token.getToken();
    }

    // ── Relatório CSV ─────────────────────────────────────────────────────────

    private String chamarRelatorioGraph(String token) throws Exception {
        var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        var req = HttpRequest.newBuilder()
                .uri(URI.create(GRAPH_REPORTS_URL))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "text/csv")
                .GET().build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() != 200)
            throw new RuntimeException("Graph API retornou " + res.statusCode() + ": " + res.body());
        return res.body();
    }

    private Map<String, StorageData> parsearCSVParaStorageMap(String csv) throws Exception {
        var result = new HashMap<String, StorageData>();
        if (csv.startsWith("﻿")) csv = csv.substring(1);

        var reader = new BufferedReader(new StringReader(csv));
        String headerLine = reader.readLine();
        if (headerLine == null) return result;

        String[] headers = parseCsvLine(headerLine);
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headers.length; i++)
            idx.put(headers[i].trim().toLowerCase(), i);

        int iUpn      = idx.getOrDefault("user principal name", 1);
        int iDeleted  = idx.getOrDefault("is deleted", 3);
        int iCreated  = idx.getOrDefault("created date", 5);
        int iActivity = idx.getOrDefault("last activity date", 6);
        int iItems    = idx.getOrDefault("item count", 7);
        int iUsed     = idx.getOrDefault("storage used (byte)", 8);
        int iLimit    = idx.getOrDefault("prohibit send/receive quota (byte)", 11);

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;
            String[] cols = parseCsvLine(line);
            if (cols.length < 4) continue;
            String upn = safeGet(cols, iUpn).toLowerCase();
            if (!upn.isEmpty())
                result.put(upn, new StorageData(
                        parseLong(safeGet(cols, iItems)),
                        parseLong(safeGet(cols, iUsed)),
                        parseLong(safeGet(cols, iLimit)),
                        "True".equalsIgnoreCase(safeGet(cols, iDeleted)),
                        safeGet(cols, iCreated),
                        safeGet(cols, iActivity)));
        }
        return result;
    }

    // ── Usuários ──────────────────────────────────────────────────────────────

    private List<UserBasico> buscarTodosUsuariosComMail(String token) {
        var result = new ArrayList<UserBasico>();
        var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        String url = GRAPH_USERS_URL;
        while (url != null) {
            try {
                var req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .header("Accept", "application/json")
                        .GET().build();
                var res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (res.statusCode() != 200) break;

                JsonNode root = mapper.readTree(res.body());
                for (JsonNode u : root.path("value")) {
                    String upn      = u.path("userPrincipalName").asText("").trim();
                    String mail     = u.path("mail").asText("").trim();
                    String userType = u.path("userType").asText("Member").trim();

                    if (upn.isEmpty() || mail.isEmpty()) continue;
                    if ("Guest".equalsIgnoreCase(userType)) continue;
                    if (upn.toLowerCase().contains("#ext#")) continue;

                    var licencaSkuIds = new ArrayList<String>();
                    JsonNode licenses = u.path("assignedLicenses");
                    if (licenses.isArray()) {
                        for (JsonNode lic : licenses) {
                            String skuId = lic.path("skuId").asText("").trim();
                            if (!skuId.isEmpty()) licencaSkuIds.add(skuId);
                        }
                    }
                    String rawSenha = u.path("lastPasswordChangeDateTime").asText("").trim();
                    String ultimaTrocaSenha = rawSenha.length() >= 10 ? rawSenha.substring(0, 10) : rawSenha;
                    result.add(new UserBasico(
                            upn,
                            u.path("displayName").asText("").trim(),
                            u.path("accountEnabled").asBoolean(true),
                            u.path("department").asText("").trim(),
                            licencaSkuIds,
                            ultimaTrocaSenha));
                }
                JsonNode next = root.path("@odata.nextLink");
                url = (!next.isMissingNode() && !next.asText().isEmpty()) ? next.asText() : null;
            } catch (Exception e) { break; }
        }
        return result;
    }

    // ── Tipo de caixa via $batch ──────────────────────────────────────────────

    private Map<String, String> buscarTiposMailboxEmLote(String token, List<UserBasico> usuarios) {
        var result = new HashMap<String, String>();
        if (usuarios.isEmpty()) return result;

        var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

        for (int inicio = 0; inicio < usuarios.size(); inicio += BATCH_SIZE) {
            List<UserBasico> lote = usuarios.subList(inicio, Math.min(inicio + BATCH_SIZE, usuarios.size()));
            try {
                ArrayNode requests = mapper.createArrayNode();
                for (int i = 0; i < lote.size(); i++) {
                    var r = mapper.createObjectNode();
                    r.put("id", String.valueOf(i));
                    r.put("method", "GET");
                    r.put("url", "/users/" + encode(lote.get(i).upn()) + "/mailboxSettings?$select=userPurpose");
                    requests.add(r);
                }
                var body = mapper.createObjectNode();
                body.set("requests", requests);

                var req = HttpRequest.newBuilder()
                        .uri(URI.create(GRAPH_BATCH_URL))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                        .build();
                var res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (res.statusCode() != 200) continue;

                JsonNode batchRes = mapper.readTree(res.body());
                for (JsonNode resp : batchRes.path("responses")) {
                    int id = resp.path("id").asInt(-1);
                    if (id < 0 || id >= lote.size()) continue;
                    String purpose = resp.path("body").path("userPurpose").asText("").trim().toLowerCase();
                    if (!purpose.isEmpty())
                        result.put(lote.get(id).upn().toLowerCase(), purpose);
                }
            } catch (Exception ignored) {}
        }
        return result;
    }

    private String encode(String upn) {
        return upn.replace("@", "%40");
    }

    private String resolverTipo(String userPurpose, boolean accountEnabled) {
        return switch (userPurpose) {
            case "shared"    -> "SharedMailbox";
            case "room"      -> "RoomMailbox";
            case "equipment" -> "EquipmentMailbox";
            case "user"      -> "UserMailbox";
            default          -> accountEnabled ? "UserMailbox" : "SharedMailbox";
        };
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private String safeGet(String[] cols, int index) {
        return (index >= 0 && index < cols.length) ? cols[index].trim() : "";
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s.replace(",", "").replace(".", "").trim()); }
        catch (Exception e) { return 0L; }
    }

    private String[] parseCsvLine(String line) {
        var tokens = new ArrayList<String>();
        var current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') { inQuotes = !inQuotes; }
            else if (c == ',' && !inQuotes) { tokens.add(current.toString()); current = new StringBuilder(); }
            else { current.append(c); }
        }
        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }
}
