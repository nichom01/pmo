package com.yourapp.dtos;

import com.yourapp.entities.Organisation;

import java.util.UUID;

public record OrganisationResponse(
        UUID id,
        String name,
        String slug,
        int issueSequence
) {
    public static OrganisationResponse from(Organisation organisation) {
        return new OrganisationResponse(
                organisation.getId(),
                organisation.getName(),
                organisation.getSlug(),
                organisation.getIssueSequence()
        );
    }
}

