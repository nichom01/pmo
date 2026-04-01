package com.yourapp.repositories;

import com.yourapp.entities.IssueActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueActivityRepository extends JpaRepository<IssueActivity, UUID> {
    List<IssueActivity> findByIssueIdOrderByCreatedAtDesc(UUID issueId);
}
