package com.yourapp.dtos;

import com.yourapp.entities.IssuePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateIssueRequest(
        @NotBlank String title,
        String description,
        @NotNull UUID workflowStateId,
        @NotNull IssuePriority priority
) {}

