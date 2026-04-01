package com.yourapp.dtos;

import com.yourapp.entities.Project;

import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID teamId,
        String name,
        String description,
        String status
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTeam().getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name()
        );
    }
}
