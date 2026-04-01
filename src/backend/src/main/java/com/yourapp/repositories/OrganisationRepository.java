package com.yourapp.repositories;

import com.yourapp.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {
    Optional<Organisation> findBySlug(String slug);
}
