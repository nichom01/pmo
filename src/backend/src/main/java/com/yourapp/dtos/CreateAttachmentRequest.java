package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateAttachmentRequest(
        @NotBlank String filename,
        @NotBlank String content,
        @NotBlank String mimeType
) {
}
