package com.deepprotech.deepproject.sprints.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeSprintStatusCommand(@NotNull UUID sprintId, @NotBlank String status) {}
