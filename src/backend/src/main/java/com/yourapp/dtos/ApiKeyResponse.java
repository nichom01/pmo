package com.yourapp.dtos;

import com.yourapp.entities.ApiKey;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        UUID userId,
        String label,
        OffsetDateTime createdAt
) {
    public static ApiKeyResponse from(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getUser().getId(),
                apiKey.getLabel(),
                apiKey.getCreatedAt()
        );
    }
}
