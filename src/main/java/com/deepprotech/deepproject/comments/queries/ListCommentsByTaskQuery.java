package com.deepprotech.deepproject.comments.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListCommentsByTaskQuery(@NotNull UUID taskId) {}
