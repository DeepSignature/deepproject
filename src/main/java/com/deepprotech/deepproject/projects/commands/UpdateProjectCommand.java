package com.deepprotech.deepproject.projects.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProjectCommand(
        @NotNull UUID projectId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description
) {}
