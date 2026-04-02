package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @NotBlank String name,
        @NotBlank String identifier
) {}

