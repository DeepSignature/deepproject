package com.deepprotech.deepproject.iam.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignUserRoleCommand(@NotNull UUID userId, @NotNull UUID roleId) {}
