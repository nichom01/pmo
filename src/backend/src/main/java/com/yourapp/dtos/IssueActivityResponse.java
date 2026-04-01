package com.yourapp.dtos;

import com.yourapp.entities.IssueActivity;

import java.util.UUID;

public record IssueActivityResponse(
        UUID id,
        UUID issueId,
        UUID actorId,
        String type,
        String fromValue,
        String toValue
) {
    public static IssueActivityResponse from(IssueActivity activity) {
        return new IssueActivityResponse(
                activity.getId(),
                activity.getIssue().getId(),
                activity.getActor().getId(),
                activity.getType(),
                activity.getFromValue(),
                activity.getToValue()
        );
    }
}
