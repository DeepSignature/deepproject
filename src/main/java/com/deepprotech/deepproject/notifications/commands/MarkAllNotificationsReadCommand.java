package com.deepprotech.deepproject.notifications.commands;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkAllNotificationsReadCommand(@NotNull UUID userId) {}
