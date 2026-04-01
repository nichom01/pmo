package com.yourapp.repositories;

import com.yourapp.entities.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findTop101ByProjectIdOrderByCreatedAtDescIdDesc(UUID projectId);
    List<Issue> findTop101ByProjectIdAndCreatedAtLessThanOrderByCreatedAtDescIdDesc(UUID projectId, OffsetDateTime createdAt);
    List<Issue> findTop101ByProjectIdAndCreatedAtEqualsAndIdLessThanOrderByCreatedAtDescIdDesc(UUID projectId, OffsetDateTime createdAt, UUID id);
    long countByProjectId(UUID projectId);
}
