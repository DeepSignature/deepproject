package com.deepprotech.deepproject.projects.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeProjectStatusCommand(@NotNull Long projectId, @NotBlank String status) {}
