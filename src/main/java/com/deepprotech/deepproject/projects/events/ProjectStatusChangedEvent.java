package com.deepprotech.deepproject.projects.events;

import java.time.Instant;
import java.util.UUID;

public record ProjectStatusChangedEvent(UUID projectId, String oldStatus, String newStatus, Instant occurredAt) {}