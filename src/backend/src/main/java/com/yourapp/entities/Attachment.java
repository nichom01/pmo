package com.yourapp.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Column(nullable = false)
    private String filename;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_size", nullable = false)
    private int fileSize;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public Issue getIssue() { return issue; }
    public void setIssue(Issue issue) { this.issue = issue; }
    public User getUploader() { return uploader; }
    public void setUploader(User uploader) { this.uploader = uploader; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public int getFileSize() { return fileSize; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
}
