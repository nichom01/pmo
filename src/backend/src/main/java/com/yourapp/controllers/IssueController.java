package com.yourapp.controllers;

import com.yourapp.dtos.AssignCycleRequest;
import com.yourapp.dtos.CreateIssueRequest;
import com.yourapp.dtos.IssueResponse;
import com.yourapp.dtos.PaginatedResponse;
import com.yourapp.dtos.UpdateIssueRequest;
import com.yourapp.security.PermissionService;
import com.yourapp.services.IssueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class IssueController {
    private final IssueService issueService;
    private final PermissionService permissionService;

    public IssueController(IssueService issueService, PermissionService permissionService) {
        this.issueService = issueService;
        this.permissionService = permissionService;
    }

    @GetMapping("/projects/{projectId}/issues")
    public PaginatedResponse<IssueResponse> list(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "25") int limit
    ) {
        return issueService.list(projectId, cursor, limit);
    }

    @PostMapping("/projects/{projectId}/issues")
    public IssueResponse create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateIssueRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateProjectResources(projectId);
        return IssueResponse.from(issueService.create(projectId, request));
    }

    @PatchMapping("/issues/{issueId}/cycle")
    public IssueResponse assignCycle(
            @PathVariable UUID issueId,
            @Valid @RequestBody AssignCycleRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateIssueResources(issueId);
        return IssueResponse.from(issueService.assignCycle(issueId, request.cycleId()));
    }

    @GetMapping("/issues/{issueId}")
    public IssueResponse get(@PathVariable UUID issueId) {
        return IssueResponse.from(issueService.get(issueId));
    }

    @PatchMapping("/issues/{issueId}")
    public IssueResponse update(
            @PathVariable UUID issueId,
            @Valid @RequestBody UpdateIssueRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateIssueResources(issueId);
        return IssueResponse.from(issueService.update(issueId, request));
    }

    @DeleteMapping("/issues/{issueId}")
    public void delete(
            @PathVariable UUID issueId,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateIssueResources(issueId);
        issueService.delete(issueId);
    }
}
