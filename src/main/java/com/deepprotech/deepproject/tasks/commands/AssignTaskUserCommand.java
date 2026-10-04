package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotNull;

public record AssignTaskUserCommand(@NotNull Long taskId, @NotNull Long userId) {}
