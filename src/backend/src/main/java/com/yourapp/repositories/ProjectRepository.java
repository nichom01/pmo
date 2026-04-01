package com.yourapp.repositories;

import com.yourapp.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByTeamId(UUID teamId);
    Optional<Project> findFirstByTeamId(UUID teamId);
}
