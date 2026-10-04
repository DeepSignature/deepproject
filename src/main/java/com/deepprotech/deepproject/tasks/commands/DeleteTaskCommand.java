package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotNull;

public record DeleteTaskCommand(@NotNull Long taskId) {}
