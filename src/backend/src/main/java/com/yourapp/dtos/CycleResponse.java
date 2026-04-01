package com.yourapp.dtos;

import com.yourapp.entities.Cycle;

import java.time.LocalDate;
import java.util.UUID;

public record CycleResponse(
        UUID id,
        UUID projectId,
        String name,
        String description,
        String status,
        LocalDate startDate,
        LocalDate endDate
) {
    public static CycleResponse from(Cycle cycle) {
        return new CycleResponse(
                cycle.getId(),
                cycle.getProject().getId(),
                cycle.getName(),
                cycle.getDescription(),
                cycle.getStatus().name(),
                cycle.getStartDate(),
                cycle.getEndDate()
        );
    }
}
