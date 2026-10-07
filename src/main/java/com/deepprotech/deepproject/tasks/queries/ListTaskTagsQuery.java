package com.deepprotech.deepproject.tasks.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListTaskTagsQuery(@NotNull UUID taskId) {}
