package com.yourapp.repositories;

import com.yourapp.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    Optional<Team> findFirstByIdentifier(String identifier);
}
