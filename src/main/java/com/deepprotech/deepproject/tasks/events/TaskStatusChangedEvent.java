package com.deepprotech.deepproject.tasks.events;

import java.time.Instant;

public record TaskStatusChangedEvent(Long taskId, String oldStatus, String newStatus, Instant occurredAt) {}