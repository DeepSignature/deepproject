package com.deepprotech.deepproject.tasks.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeTaskStatusCommand(@NotNull UUID taskId, @NotBlank String status) {}
