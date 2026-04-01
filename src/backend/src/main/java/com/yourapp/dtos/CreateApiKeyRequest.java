package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateApiKeyRequest(
        @NotBlank String label
) {
}
