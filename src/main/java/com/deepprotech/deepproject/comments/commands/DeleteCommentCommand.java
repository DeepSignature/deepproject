package com.deepprotech.deepproject.comments.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteCommentCommand(@NotNull UUID commentId) {}
