package com.deepprotech.deepproject.comments.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(@NotBlank String content) {}
