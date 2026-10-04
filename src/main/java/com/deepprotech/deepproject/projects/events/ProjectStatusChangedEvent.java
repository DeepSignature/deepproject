package com.deepprotech.deepproject.projects.events;

import java.time.Instant;

public record ProjectStatusChangedEvent(Long projectId, String oldStatus, String newStatus, Instant occurredAt) {}