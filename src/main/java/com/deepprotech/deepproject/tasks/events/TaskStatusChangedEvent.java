package com.deepprotech.deepproject.tasks.events;

import java.time.Instant;
import java.util.UUID;

public record TaskStatusChangedEvent(UUID taskId, String oldStatus, String newStatus, Instant occurredAt) {}