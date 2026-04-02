package com.yourapp.services;

import com.yourapp.dtos.CreateAttachmentRequest;
import com.yourapp.entities.Attachment;
import com.yourapp.entities.Issue;
import com.yourapp.entities.User;
import com.yourapp.exceptions.NotFoundException;
import com.yourapp.repositories.AttachmentRepository;
import com.yourapp.repositories.IssueRepository;
import com.yourapp.repositories.UserRepository;
import com.yourapp.storage.StorageService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final IssueActivityService issueActivityService;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository,
            StorageService storageService,
            IssueActivityService issueActivityService
    ) {
        this.attachmentRepository = attachmentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.issueActivityService = issueActivityService;
    }

    public List<Attachment> listForIssue(UUID issueId) {
        return attachmentRepository.findByIssueIdOrderByCreatedAtDesc(issueId);
    }

    public Attachment create(UUID issueId, CreateAttachmentRequest request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found: " + issueId));
        User uploader = userRepository.findByEmail("demo@acme.dev")
                .orElseThrow(() -> new NotFoundException("Demo uploader not found"));

        byte[] bytes = request.content().getBytes(StandardCharsets.UTF_8);
        String fileUrl = storageService.save(request.filename(), bytes, request.mimeType());

        Attachment attachment = new Attachment();
        attachment.setIssue(issue);
        attachment.setUploader(uploader);
        attachment.setFilename(request.filename());
        attachment.setMimeType(request.mimeType());
        attachment.setFileSize(bytes.length);
        attachment.setFileUrl(fileUrl);
        Attachment saved = attachmentRepository.save(attachment);
        issueActivityService.record(issue, uploader, "attachment_added", null, request.filename());
        return saved;
    }

    public void delete(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("Attachment not found: " + attachmentId));
        attachmentRepository.delete(attachment);
    }
}
