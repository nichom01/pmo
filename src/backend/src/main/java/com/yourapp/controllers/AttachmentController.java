package com.yourapp.controllers;

import com.yourapp.dtos.AttachmentResponse;
import com.yourapp.dtos.CreateAttachmentRequest;
import com.yourapp.security.PermissionService;
import com.yourapp.services.AttachmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final PermissionService permissionService;

    public AttachmentController(AttachmentService attachmentService, PermissionService permissionService) {
        this.attachmentService = attachmentService;
        this.permissionService = permissionService;
    }

    @GetMapping("/issues/{issueId}/attachments")
    public List<AttachmentResponse> list(@PathVariable UUID issueId) {
        return attachmentService.listForIssue(issueId).stream().map(AttachmentResponse::from).toList();
    }

    @PostMapping("/issues/{issueId}/attachments")
    public AttachmentResponse create(
            @PathVariable UUID issueId,
            @Valid @RequestBody CreateAttachmentRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateTeamResources(role);
        return AttachmentResponse.from(attachmentService.create(issueId, request));
    }
}
