package com.yourapp.dtos;

import com.yourapp.entities.WorkflowState;

import java.util.UUID;

public record WorkflowStateResponse(
        UUID id,
        UUID teamId,
        String name,
        String color,
        String type,
        int position
) {
    public static WorkflowStateResponse from(WorkflowState state) {
        return new WorkflowStateResponse(
                state.getId(),
                state.getTeam().getId(),
                state.getName(),
                state.getColor(),
                state.getType().name(),
                state.getPosition()
        );
    }
}
