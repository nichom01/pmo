package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateIssueRequest(
        @NotBlank String title,
        String description,
        @NotNull UUID workflowStateId
) {
}
