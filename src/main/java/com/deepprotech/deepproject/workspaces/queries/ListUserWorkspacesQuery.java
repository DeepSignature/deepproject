package com.deepprotech.deepproject.workspaces.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListUserWorkspacesQuery(@NotNull UUID userId, int limit, @Nullable String cursor) {}
