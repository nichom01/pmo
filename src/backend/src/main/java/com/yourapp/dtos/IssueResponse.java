package com.yourapp.dtos;

import com.yourapp.entities.Issue;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IssueResponse(
        UUID id,
        UUID projectId,
        UUID teamId,
        UUID workflowStateId,
        UUID cycleId,
        int sequenceNumber,
        String title,
        String description,
        String priority,
        OffsetDateTime createdAt
) {
    public static IssueResponse from(Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getProject().getId(),
                issue.getTeam().getId(),
                issue.getWorkflowState().getId(),
                issue.getCycle() == null ? null : issue.getCycle().getId(),
                issue.getSequenceNumber(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getPriority().name(),
                issue.getCreatedAt()
        );
    }
}
