package com.deepprotech.deepproject.iam.commands;

import jakarta.validation.constraints.NotNull;

public record DeactivateUserCommand(@NotNull Long userId) {}
