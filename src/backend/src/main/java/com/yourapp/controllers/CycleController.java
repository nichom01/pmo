package com.yourapp.controllers;

import com.yourapp.dtos.CreateCycleRequest;
import com.yourapp.dtos.CycleResponse;
import com.yourapp.dtos.UpdateCycleRequest;
import com.yourapp.security.PermissionService;
import com.yourapp.services.CycleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CycleController {
    private final CycleService cycleService;
    private final PermissionService permissionService;

    public CycleController(CycleService cycleService, PermissionService permissionService) {
        this.cycleService = cycleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/projects/{projectId}/cycles")
    public List<CycleResponse> list(@PathVariable UUID projectId) {
        return cycleService.listForProject(projectId).stream().map(CycleResponse::from).toList();
    }

    @PostMapping("/projects/{projectId}/cycles")
    public CycleResponse create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateCycleRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateProjectResources(projectId);
        return CycleResponse.from(cycleService.create(projectId, request));
    }

    @PatchMapping("/cycles/{cycleId}")
    public CycleResponse update(
            @PathVariable UUID cycleId,
            @Valid @RequestBody UpdateCycleRequest request,
            @RequestHeader(name = "X-Team-Role", defaultValue = "member") String role
    ) {
        permissionService.assertCanMutateCycleResources(cycleId);
        return CycleResponse.from(cycleService.update(cycleId, request));
    }

    @GetMapping("/cycles/{cycleId}")
    public CycleResponse get(@PathVariable UUID cycleId) {
        return CycleResponse.from(cycleService.get(cycleId));
    }
}
