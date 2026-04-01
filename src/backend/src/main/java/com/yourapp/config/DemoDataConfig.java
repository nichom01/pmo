package com.yourapp.config;

import com.yourapp.entities.*;
import com.yourapp.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner seedDemoData(
            OrganisationRepository organisationRepository,
            TeamRepository teamRepository,
            ProjectRepository projectRepository,
            WorkflowStateRepository workflowStateRepository,
            UserRepository userRepository
    ) {
        return args -> {
            userRepository.findByEmail("demo@acme.dev").orElseGet(() -> {
                User user = new User();
                user.setEmail("demo@acme.dev");
                user.setUsername("demo");
                user.setPasswordHash("$2a$10$localdevlocaldevlocaldevlocaldevlocaldev");
                return userRepository.save(user);
            });

            Organisation org = organisationRepository.findBySlug("acme").orElseGet(() -> {
                Organisation created = new Organisation();
                created.setName("Acme");
                created.setSlug("acme");
                return organisationRepository.save(created);
            });

            Team team = teamRepository.findFirstByIdentifier("eng").orElseGet(() -> {
                Team created = new Team();
                created.setOrganisation(org);
                created.setName("Engineering");
                created.setIdentifier("eng");
                return teamRepository.save(created);
            });

            projectRepository.findFirstByTeamId(team.getId()).orElseGet(() -> {
                Project project = new Project();
                project.setTeam(team);
                project.setName("PMO Core");
                project.setDescription("Default local project");
                project.setStatus(ProjectStatus.active);
                return projectRepository.save(project);
            });

            if (workflowStateRepository.findByTeamIdOrderByPositionAsc(team.getId()).isEmpty()) {
                workflowStateRepository.save(newState(team, "Backlog", "#9ca3af", WorkflowStateType.backlog, 0));
                workflowStateRepository.save(newState(team, "Todo", "#60a5fa", WorkflowStateType.unstarted, 1));
                workflowStateRepository.save(newState(team, "In Progress", "#f59e0b", WorkflowStateType.started, 2));
                workflowStateRepository.save(newState(team, "Done", "#10b981", WorkflowStateType.completed, 3));
            }
        };
    }

    private WorkflowState newState(Team team, String name, String color, WorkflowStateType type, int position) {
        WorkflowState state = new WorkflowState();
        state.setTeam(team);
        state.setName(name);
        state.setColor(color);
        state.setType(type);
        state.setPosition(position);
        return state;
    }
}
