package com.yourapp.dtos;

import com.yourapp.entities.OrganisationMembership;

import java.util.UUID;

public record OrganisationMemberResponse(
        UUID userId,
        String email,
        String username,
        String role
) {
    public static OrganisationMemberResponse from(OrganisationMembership membership) {
        return new OrganisationMemberResponse(
                membership.getUser().getId(),
                membership.getUser().getEmail(),
                membership.getUser().getUsername(),
                membership.getRole()
        );
    }
}

