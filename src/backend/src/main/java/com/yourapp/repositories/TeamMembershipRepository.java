package com.yourapp.repositories;

import com.yourapp.entities.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, UUID> {
    List<TeamMembership> findByTeamId(UUID teamId);
}

