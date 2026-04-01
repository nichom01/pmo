package com.yourapp.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String body
) {
}
