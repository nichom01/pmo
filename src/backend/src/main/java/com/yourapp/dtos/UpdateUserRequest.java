package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String username,
        String avatarUrl,
        @NotBlank String timezone
) {}

