package com.yourapp.services;

import com.yourapp.dtos.CreateCycleRequest;
import com.yourapp.dtos.UpdateCycleRequest;
import com.yourapp.entities.Cycle;
import com.yourapp.entities.CycleStatus;
import com.yourapp.entities.Project;
import com.yourapp.exceptions.ForbiddenException;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.CycleRepository;
import com.yourapp.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CycleService {
    private final CycleRepository cycleRepository;
    private final ProjectRepository projectRepository;

    public CycleService(CycleRepository cycleRepository, ProjectRepository projectRepository) {
        this.cycleRepository = cycleRepository;
        this.projectRepository = projectRepository;
    }

    public List<Cycle> listForProject(UUID projectId) {
        return cycleRepository.findByProjectIdOrderByStartDateDesc(projectId);
    }

    public Cycle get(UUID cycleId) {
        return cycleRepository.findById(cycleId)
                .orElseThrow(() -> new NotFoundException("Cycle not found: " + cycleId));
    }

    public Cycle create(UUID projectId, CreateCycleRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
        Cycle cycle = new Cycle();
        cycle.setProject(project);
        cycle.setName(request.name());
        cycle.setDescription(request.description());
        cycle.setStartDate(request.startDate());
        cycle.setEndDate(request.endDate());
        cycle.setStatus(CycleStatus.draft);
        return cycleRepository.save(cycle);
    }

    public Cycle update(UUID cycleId, UpdateCycleRequest request) {
        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new NotFoundException("Cycle not found: " + cycleId));
        if (request.status() == CycleStatus.active) {
            var currentActive = cycleRepository.findFirstByProjectIdAndStatus(cycle.getProject().getId(), CycleStatus.active);
            if (currentActive.isPresent() && !currentActive.get().getId().equals(cycle.getId())) {
                throw new ForbiddenException("Only one active cycle is allowed per project.");
            }
        }
        cycle.setStatus(request.status());
        return cycleRepository.save(cycle);
    }
}
