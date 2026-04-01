package com.yourapp.dtos;

import com.yourapp.entities.NotificationPreference;

import java.util.UUID;

public record NotificationPreferenceResponse(
        UUID id,
        UUID userId,
        UUID organisationId,
        String eventType,
        String channel,
        boolean enabled
) {
    public static NotificationPreferenceResponse from(NotificationPreference p) {
        return new NotificationPreferenceResponse(
                p.getId(),
                p.getUser().getId(),
                p.getOrganisation().getId(),
                p.getEventType(),
                p.getChannel(),
                p.isEnabled()
        );
    }
}
