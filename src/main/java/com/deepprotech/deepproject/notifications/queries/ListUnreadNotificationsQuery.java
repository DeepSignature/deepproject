package com.deepprotech.deepproject.notifications.queries;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListUnreadNotificationsQuery(@NotNull UUID userId) {}
