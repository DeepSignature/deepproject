package com.deepprotech.deepproject.tasks.events;

import java.time.Instant;

public record TaskCreatedEvent(Long taskId, String title, Long projectId, Instant occurredAt) {}