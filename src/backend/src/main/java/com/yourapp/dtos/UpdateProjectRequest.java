package com.yourapp.dtos;

import com.yourapp.entities.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectRequest(
        @NotBlank String name,
        String description,
        @NotNull ProjectStatus status
) {}

