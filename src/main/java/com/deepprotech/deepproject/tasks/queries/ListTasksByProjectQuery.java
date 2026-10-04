package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;

public record ListTasksByProjectQuery(@NotNull Long projectId) {}
