package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListSubtasksQuery(@NotNull UUID parentTaskId, int limit, @Nullable String cursor) {}
