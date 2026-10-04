package com.deepprotech.deepproject.iam.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserCommand(
        @NotNull Long userId,
        @Size(max = 200) String displayName
) {}