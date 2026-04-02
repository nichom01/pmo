package com.yourapp.repositories;

import com.yourapp.entities.OrganisationMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganisationMembershipRepository extends JpaRepository<OrganisationMembership, UUID> {
    List<OrganisationMembership> findByOrganisationId(UUID organisationId);

    boolean existsByOrganisationIdAndUserId(UUID organisationId, UUID userId);
}

