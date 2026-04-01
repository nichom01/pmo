package com.yourapp.repositories;

import com.yourapp.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByIssueIdOrderByCreatedAtDesc(UUID issueId);
}
