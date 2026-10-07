package com.deepprotech.deepproject.workspaces.events;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceCreatedEvent(UUID workspaceId, String name, UUID ownerId, Instant occurredAt) {}