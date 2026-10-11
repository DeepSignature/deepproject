package com.deepprotech.deepproject.sprints.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateSprintCommand(
        @NotNull UUID projectId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        Instant startDate,
        Instant endDate
) {}
