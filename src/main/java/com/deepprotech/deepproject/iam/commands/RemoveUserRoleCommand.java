package com.deepprotech.deepproject.iam.commands;

import jakarta.validation.constraints.NotNull;

public record RemoveUserRoleCommand(@NotNull Long userId, @NotNull Long roleId) {}
