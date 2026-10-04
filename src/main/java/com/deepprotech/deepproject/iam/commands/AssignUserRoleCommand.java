package com.deepprotech.deepproject.iam.commands;

import jakarta.validation.constraints.NotNull;

public record AssignUserRoleCommand(@NotNull Long userId, @NotNull Long roleId) {}
