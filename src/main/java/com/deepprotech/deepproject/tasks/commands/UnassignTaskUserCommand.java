package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotNull;

public record UnassignTaskUserCommand(@NotNull Long taskId, @NotNull Long userId) {}
