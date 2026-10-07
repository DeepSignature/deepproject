package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTaskUserCommand(@NotNull UUID taskId, @NotNull UUID userId) {}
