package com.yourapp.dtos;

import com.yourapp.entities.Notification;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID recipientId,
        UUID actorId,
        UUID issueId,
        String type,
        OffsetDateTime readAt,
        OffsetDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipient().getId(),
                notification.getActor() == null ? null : notification.getActor().getId(),
                notification.getIssue() == null ? null : notification.getIssue().getId(),
                notification.getType(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}
