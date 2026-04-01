package com.yourapp.dtos;

import com.yourapp.entities.CycleStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCycleRequest(
        @NotNull CycleStatus status
) {
}
