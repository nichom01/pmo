package com.yourapp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class CursorService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String encode(OffsetDateTime createdAt, UUID id) {
        try {
            String json = objectMapper.writeValueAsString(Map.of("created_at", createdAt.toString(), "id", id.toString()));
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to encode cursor", ex);
        }
    }

    public Cursor decode(String cursor) {
        try {
            byte[] decoded = Base64.getDecoder().decode(cursor);
            Map<String, String> map = objectMapper.readValue(decoded, new TypeReference<>() {});
            return new Cursor(OffsetDateTime.parse(map.get("created_at")), UUID.fromString(map.get("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid cursor");
        }
    }

    public record Cursor(OffsetDateTime createdAt, UUID id) {}
}
