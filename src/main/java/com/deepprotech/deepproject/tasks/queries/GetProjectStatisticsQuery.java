package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record GetProjectStatisticsQuery(@NotNull UUID projectId, @NotNull Instant from, @NotNull Instant to) {}
