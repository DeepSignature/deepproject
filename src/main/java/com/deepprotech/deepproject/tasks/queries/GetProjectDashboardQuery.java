package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetProjectDashboardQuery(@NotNull UUID projectId, @Nullable Instant from, @Nullable Instant to,
                                       int taskLimit, @Nullable String taskCursor,
                                       @Nullable List<String> status, @Nullable List<String> priority) {}
