package com.deepprotech.deepproject.tasks.events;

import java.time.Instant;
import java.util.UUID;

public record TaskAssignedEvent(UUID taskId, UUID userId, Instant occurredAt) {}