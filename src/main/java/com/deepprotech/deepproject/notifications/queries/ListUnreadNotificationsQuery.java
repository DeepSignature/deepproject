package com.deepprotech.deepproject.notifications.queries;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

public record ListUnreadNotificationsQuery(@NotNull UUID userId, int limit, @Nullable String cursor) {}
