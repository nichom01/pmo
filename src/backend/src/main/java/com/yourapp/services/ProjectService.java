package com.yourapp.services;

import com.yourapp.dtos.CreateProjectRequest;
import com.yourapp.dtos.UpdateProjectRequest;
import com.yourapp.entities.Project;
import com.yourapp.entities.ProjectStatus;
import com.yourapp.entities.Team;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.ProjectRepository;
import com.yourapp.repositories.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    public ProjectService(ProjectRepository projectRepository, TeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }

    public List<Project> listForTeam(UUID teamId) {
        return projectRepository.findByTeamId(teamId);
    }

    public Project create(UUID teamId, CreateProjectRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found: " + teamId));
        Project project = new Project();
        project.setTeam(team);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(ProjectStatus.planning);
        return projectRepository.save(project);
    }

    public Project get(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
    }

    public Project update(UUID projectId, UpdateProjectRequest request) {
        Project project = get(projectId);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());
        return projectRepository.save(project);
    }

    public void delete(UUID projectId) {
        Project project = get(projectId);
        project.setDeletedAt(OffsetDateTime.now());
        projectRepository.save(project);
    }
}
