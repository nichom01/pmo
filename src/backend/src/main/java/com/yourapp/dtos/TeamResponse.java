package com.yourapp.dtos;

import com.yourapp.entities.Team;

import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID organisationId,
        String name,
        String identifier
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getOrganisation().getId(),
                team.getName(),
                team.getIdentifier()
        );
    }
}

