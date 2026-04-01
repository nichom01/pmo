package com.yourapp.controllers;

import com.yourapp.dtos.CreateProjectRequest;
import com.yourapp.dtos.ProjectResponse;
import com.yourapp.security.PermissionService;
import com.yourapp.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {
    private final ProjectService projectService;
    private final PermissionService permissionService;

    public ProjectController(ProjectService projectService, PermissionService permissionService) {
        this.projectService = projectService;
        this.permissionService = permissionService;
    }

    @GetMapping("/teams/{teamId}/projects")
    public List<ProjectResponse> list(@PathVariable UUID teamId) {
        return projectService.listForTeam(teamId).stream().map(ProjectResponse::from).toList();
    }

    @PostMapping("/teams/{teamId}/projects")
    public ProjectResponse create(
            @PathVariable UUID teamId,
            @Valid @RequestBody CreateProjectRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateTeamResources(role);
        return ProjectResponse.from(projectService.create(teamId, request));
    }
}
