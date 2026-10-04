package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskCommand(
        @NotNull Long taskId,
        @NotBlank @Size(max = 500) String title,
        @Size(max = 5000) String description,
        @NotBlank String priority
) {}
