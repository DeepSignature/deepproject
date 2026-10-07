package com.deepprotech.deepproject.comments.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetCommentByIdQuery(@NotNull UUID commentId) {}
