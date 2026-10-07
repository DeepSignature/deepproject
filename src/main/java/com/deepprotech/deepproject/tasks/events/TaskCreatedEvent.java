package com.deepprotech.deepproject.tasks.events;

import java.time.Instant;
import java.util.UUID;

public record TaskCreatedEvent(UUID taskId, String title, UUID projectId, Instant occurredAt) {}