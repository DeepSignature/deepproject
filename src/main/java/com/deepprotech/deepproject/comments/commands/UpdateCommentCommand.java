package com.deepprotech.deepproject.comments.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentCommand(@NotNull Long commentId, @NotBlank String content) {}
