package com.yourapp.dtos;

public record CreateApiKeyResponse(
        ApiKeyResponse apiKey,
        String rawKey
) {
}
