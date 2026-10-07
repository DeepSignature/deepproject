package com.deepprotech.deepproject.projects.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeProjectStatusCommand(@NotNull UUID projectId, @NotBlank String status) {}
