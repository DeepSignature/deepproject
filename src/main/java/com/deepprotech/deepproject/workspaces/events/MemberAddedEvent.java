package com.deepprotech.deepproject.workspaces.events;

import java.time.Instant;
import java.util.UUID;

public record MemberAddedEvent(UUID workspaceId, UUID userId, String role, Instant occurredAt) {}