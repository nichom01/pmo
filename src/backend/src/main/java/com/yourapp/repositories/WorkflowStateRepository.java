package com.yourapp.repositories;

import com.yourapp.entities.WorkflowState;
import com.yourapp.entities.WorkflowStateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowStateRepository extends JpaRepository<WorkflowState, UUID> {
    List<WorkflowState> findByTeamIdOrderByPositionAsc(UUID teamId);
    Optional<WorkflowState> findFirstByTeamIdAndTypeOrderByPositionAsc(UUID teamId, WorkflowStateType type);
}
