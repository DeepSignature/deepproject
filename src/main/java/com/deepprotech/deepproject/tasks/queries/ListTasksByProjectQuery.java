package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListTasksByProjectQuery(@NotNull UUID projectId, int limit, @Nullable String cursor) {}
