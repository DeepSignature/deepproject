package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

public record GetAssigneeDashboardQuery(@NotNull UUID projectId, @NotNull UUID assigneeId,
                                        @Nullable Instant from, @Nullable Instant to,
                                        int limit, @Nullable String cursor) {}
