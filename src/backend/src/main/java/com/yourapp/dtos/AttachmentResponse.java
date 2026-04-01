package com.yourapp.dtos;

import com.yourapp.entities.Attachment;

import java.util.UUID;

public record AttachmentResponse(
        UUID id,
        UUID issueId,
        UUID uploaderId,
        String filename,
        String fileUrl,
        int fileSize,
        String mimeType
) {
    public static AttachmentResponse from(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getIssue().getId(),
                attachment.getUploader().getId(),
                attachment.getFilename(),
                attachment.getFileUrl(),
                attachment.getFileSize(),
                attachment.getMimeType()
        );
    }
}
