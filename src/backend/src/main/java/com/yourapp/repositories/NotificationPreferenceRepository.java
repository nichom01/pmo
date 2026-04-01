package com.yourapp.repositories;

import com.yourapp.entities.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    List<NotificationPreference> findByUserIdOrderByEventTypeAscChannelAsc(UUID userId);
    Optional<NotificationPreference> findByUserIdAndEventTypeAndChannel(UUID userId, String eventType, String channel);
}
