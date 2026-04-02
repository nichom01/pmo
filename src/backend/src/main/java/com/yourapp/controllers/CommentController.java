package com.yourapp.controllers;

import com.yourapp.dtos.CommentResponse;
import com.yourapp.dtos.CreateCommentRequest;
import com.yourapp.dtos.UpdateCommentRequest;
import com.yourapp.security.PermissionService;
import com.yourapp.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;
    private final PermissionService permissionService;

    public CommentController(CommentService commentService, PermissionService permissionService) {
        this.commentService = commentService;
        this.permissionService = permissionService;
    }

    @GetMapping("/issues/{issueId}/comments")
    public List<CommentResponse> list(@PathVariable UUID issueId) {
        return commentService.listForIssue(issueId).stream().map(CommentResponse::from).toList();
    }

    @PostMapping("/issues/{issueId}/comments")
    public CommentResponse create(
            @PathVariable UUID issueId,
            @Valid @RequestBody CreateCommentRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateIssueResources(issueId);
        return CommentResponse.from(commentService.create(issueId, request));
    }

    @PatchMapping("/comments/{commentId}")
    public CommentResponse update(
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateCommentResources(commentId);
        return CommentResponse.from(commentService.update(commentId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public CommentResponse delete(
            @PathVariable UUID commentId,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateCommentResources(commentId);
        return CommentResponse.from(commentService.softDelete(commentId));
    }
}
