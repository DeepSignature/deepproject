package com.deepprotech.deepproject.sprints.events;

import java.time.Instant;
import java.util.UUID;

public record SprintCreatedEvent(UUID sprintId, String name, UUID projectId, Instant occurredAt) {}
