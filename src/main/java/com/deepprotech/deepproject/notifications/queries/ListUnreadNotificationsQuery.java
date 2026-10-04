package com.deepprotech.deepproject.notifications.queries;

import jakarta.validation.constraints.NotNull;

public record ListUnreadNotificationsQuery(@NotNull Long userId) {}
