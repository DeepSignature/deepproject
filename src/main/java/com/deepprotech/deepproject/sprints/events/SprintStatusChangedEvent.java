package com.deepprotech.deepproject.sprints.events;

import java.time.Instant;
import java.util.UUID;

public record SprintStatusChangedEvent(UUID sprintId, String oldStatus, String newStatus, Instant occurredAt) {}
