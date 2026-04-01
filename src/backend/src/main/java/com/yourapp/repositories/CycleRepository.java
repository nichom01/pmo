package com.yourapp.repositories;

import com.yourapp.entities.Cycle;
import com.yourapp.entities.CycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CycleRepository extends JpaRepository<Cycle, UUID> {
    List<Cycle> findByProjectIdOrderByStartDateDesc(UUID projectId);
    Optional<Cycle> findFirstByProjectIdAndStatus(UUID projectId, CycleStatus status);
}
