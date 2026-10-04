package com.deepprotech.deepproject.comments.commands;

import jakarta.validation.constraints.NotNull;

public record DeleteCommentCommand(@NotNull Long commentId) {}
