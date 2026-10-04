package com.deepprotech.deepproject.projects.events;

import java.time.Instant;

public record ProjectCreatedEvent(Long projectId, String name, Long workspaceId, Instant occurredAt) {}