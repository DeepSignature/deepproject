package com.deepprotech.deepproject.notifications.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationCommand(
        @NotNull Long userId,
        @NotBlank @Size(max = 300) String title,
        @NotBlank String message,
        @NotBlank String notificationType,
        String entityType,
        Long entityId
) {}
