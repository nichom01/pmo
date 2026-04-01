package com.yourapp.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignCycleRequest(
        @NotNull UUID cycleId
) {
}
