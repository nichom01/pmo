package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateNotificationPreferenceRequest(
        @NotBlank String eventType,
        @NotBlank String channel,
        boolean enabled
) {
}
