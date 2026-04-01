package com.yourapp.dtos;

import com.yourapp.entities.Comment;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID issueId,
        UUID authorId,
        String body,
        OffsetDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getIssue().getId(),
                comment.getAuthor().getId(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}
