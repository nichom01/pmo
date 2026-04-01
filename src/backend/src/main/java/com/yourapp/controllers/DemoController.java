package com.yourapp.controllers;

import com.yourapp.entities.Project;
import com.yourapp.entities.Team;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.CycleRepository;
import com.yourapp.repositories.ProjectRepository;
import com.yourapp.repositories.TeamRepository;
import com.yourapp.repositories.WorkflowStateRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final WorkflowStateRepository workflowStateRepository;
    private final CycleRepository cycleRepository;

    public DemoController(
            TeamRepository teamRepository,
            ProjectRepository projectRepository,
            WorkflowStateRepository workflowStateRepository,
            CycleRepository cycleRepository
    ) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.workflowStateRepository = workflowStateRepository;
        this.cycleRepository = cycleRepository;
    }

    @GetMapping("/context")
    public Map<String, Object> context() {
        Team team = teamRepository.findFirstByIdentifier("eng")
                .orElseThrow(() -> new NotFoundException("Demo team not found"));
        Project project = projectRepository.findFirstByTeamId(team.getId())
                .orElseThrow(() -> new NotFoundException("Demo project not found"));
        var workflowStates = workflowStateRepository.findByTeamIdOrderByPositionAsc(team.getId());
        var cycles = cycleRepository.findByProjectIdOrderByStartDateDesc(project.getId());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orgSlug", "acme");
        response.put("teamId", team.getId());
        response.put("teamIdentifier", "eng");
        response.put("projectId", project.getId());
        response.put("workflowStateId", workflowStates.isEmpty() ? null : workflowStates.get(0).getId());
        response.put("cycleId", cycles.isEmpty() ? null : cycles.get(0).getId());
        return response;
    }
}
