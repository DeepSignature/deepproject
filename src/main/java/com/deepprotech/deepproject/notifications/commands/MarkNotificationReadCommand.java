package com.deepprotech.deepproject.notifications.commands;

import jakarta.validation.constraints.NotNull;

public record MarkNotificationReadCommand(@NotNull Long notificationId) {}
