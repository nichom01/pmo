package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateOrganisationRequest(
        @NotBlank String name,
        @NotBlank String slug
) {}

