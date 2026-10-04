package com.deepprotech.deepproject.notifications.queries;

import jakarta.validation.constraints.NotNull;

public record ListUserNotificationsQuery(@NotNull Long userId) {}
