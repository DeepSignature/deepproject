package com.deepprotech.deepproject.comments.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCommentCommand(
        @NotNull UUID taskId,
        @NotNull UUID authorId,
        @NotBlank String content
) {}
