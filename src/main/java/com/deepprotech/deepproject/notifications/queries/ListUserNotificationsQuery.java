package com.deepprotech.deepproject.notifications.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListUserNotificationsQuery(@NotNull UUID userId) {}
