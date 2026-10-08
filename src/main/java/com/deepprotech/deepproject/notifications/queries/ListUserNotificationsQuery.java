package com.deepprotech.deepproject.notifications.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListUserNotificationsQuery(@NotNull UUID userId, int limit, @Nullable String cursor) {}
