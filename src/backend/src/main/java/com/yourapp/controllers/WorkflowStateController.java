package com.yourapp.controllers;

import com.yourapp.dtos.WorkflowStateResponse;
import com.yourapp.services.WorkflowStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WorkflowStateController {
    private final WorkflowStateService workflowStateService;

    public WorkflowStateController(WorkflowStateService workflowStateService) {
        this.workflowStateService = workflowStateService;
    }

    @GetMapping("/teams/{teamId}/workflow-states")
    public List<WorkflowStateResponse> list(@PathVariable UUID teamId) {
        return workflowStateService.listForTeam(teamId).stream().map(WorkflowStateResponse::from).toList();
    }
}
