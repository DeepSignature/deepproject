package com.deepprotech.deepproject.iam.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateUserCommand(
        @NotNull UUID userId,
        @Size(max = 200) String displayName
) {}