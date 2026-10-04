package com.deepprotech.deepproject.workspaces.events;

import java.time.Instant;

public record WorkspaceCreatedEvent(Long workspaceId, String name, Long ownerId, Instant occurredAt) {}