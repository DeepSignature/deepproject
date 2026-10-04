package com.deepprotech.deepproject.comments.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentCommand(
        @NotNull Long taskId,
        @NotNull Long authorId,
        @NotBlank String content
) {}
