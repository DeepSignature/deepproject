package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListTasksByProjectQuery(@NotNull UUID projectId) {}
