package com.deepprotech.deepproject.notifications.commands;

import jakarta.validation.constraints.NotNull;

public record MarkAllNotificationsReadCommand(@NotNull Long userId) {}
