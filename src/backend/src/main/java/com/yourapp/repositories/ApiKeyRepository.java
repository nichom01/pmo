package com.yourapp.repositories;

import com.yourapp.entities.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
