package br.com.techgold.security.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte List<String> para/de TEXT no banco de dados.
 *
 * Leitura (banco → Java):
 *   - JSON array  ["a","b"]  → List.of("a","b")
 *   - Legado pipe "a||b"     → List.of("a","b")
 *   - Valor único "a"        → List.of("a")
 *
 * Escrita (Java → banco): sempre JSON array.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            return String.join("||", list);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new ArrayList<>();
        String s = dbData.trim();
        // Formato JSON array
        if (s.startsWith("[")) {
            try {
                return MAPPER.readValue(s, new TypeReference<List<String>>() {});
            } catch (Exception ignored) {}
        }
        // Formato legado separado por ||
        if (s.contains("||")) {
            return Arrays.stream(s.split("\\|\\|"))
                    .map(String::trim)
                    .filter(v -> !v.isBlank())
                    .collect(Collectors.toList());
        }
        // Valor único
        return new ArrayList<>(List.of(s));
    }
}