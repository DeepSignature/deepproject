package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;

public record ListTaskAssigneesQuery(@NotNull Long taskId) {}
