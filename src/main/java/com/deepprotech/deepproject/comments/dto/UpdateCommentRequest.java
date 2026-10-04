package com.deepprotech.deepproject.comments.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(@NotBlank String content) {}
