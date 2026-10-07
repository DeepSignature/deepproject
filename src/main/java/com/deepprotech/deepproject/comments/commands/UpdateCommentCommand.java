package com.deepprotech.deepproject.comments.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCommentCommand(@NotNull UUID commentId, @NotBlank String content) {}
