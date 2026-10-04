package com.deepprotech.deepproject.tasks.events;

import java.time.Instant;

public record TaskAssignedEvent(Long taskId, Long userId, Instant occurredAt) {}