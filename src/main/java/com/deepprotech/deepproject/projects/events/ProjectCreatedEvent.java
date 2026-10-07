package com.deepprotech.deepproject.projects.events;

import java.time.Instant;
import java.util.UUID;

public record ProjectCreatedEvent(UUID projectId, String name, UUID workspaceId, Instant occurredAt) {}