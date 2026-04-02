package com.yourapp.controllers;

import com.yourapp.dtos.CreateProjectRequest;
import com.yourapp.dtos.ProjectResponse;
import com.yourapp.dtos.UpdateProjectRequest;
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
        permissionService.assertCanMutateTeamResources(teamId);
        return ProjectResponse.from(projectService.create(teamId, request));
    }

    @GetMapping("/projects/{projectId}")
    public ProjectResponse get(@PathVariable UUID projectId) {
        return ProjectResponse.from(projectService.get(projectId));
    }

    @PatchMapping("/projects/{projectId}")
    public ProjectResponse update(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateProjectResources(projectId);
        return ProjectResponse.from(projectService.update(projectId, request));
    }

    @DeleteMapping("/projects/{projectId}")
    public void delete(
            @PathVariable UUID projectId,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateProjectResources(projectId);
        projectService.delete(projectId);
    }
}
