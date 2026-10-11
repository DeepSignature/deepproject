package com.deepprotech.deepproject.sprints.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListSprintsByProjectQuery(@NotNull UUID projectId, int limit, @Nullable String cursor) {}
