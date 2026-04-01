package com.yourapp.services;

import com.yourapp.entities.WorkflowState;
import com.yourapp.repositories.WorkflowStateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkflowStateService {
    private final WorkflowStateRepository workflowStateRepository;

    public WorkflowStateService(WorkflowStateRepository workflowStateRepository) {
        this.workflowStateRepository = workflowStateRepository;
    }

    public List<WorkflowState> listForTeam(UUID teamId) {
        return workflowStateRepository.findByTeamIdOrderByPositionAsc(teamId);
    }
}
