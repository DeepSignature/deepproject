package com.deepprotech.deepproject.tasks.commands;

import com.deepprotech.deepproject.tasks.constants.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeTaskStatusCommand(
        @NotNull UUID taskId,
        @NotBlank TaskStatus status
) {}
