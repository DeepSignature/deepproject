package com.deepprotech.deepproject.comments.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListCommentsByTaskQuery(@NotNull UUID taskId, int limit, @Nullable String cursor) {}
