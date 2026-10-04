package com.deepprotech.deepproject.workspaces.events;

import java.time.Instant;

public record MemberAddedEvent(Long workspaceId, Long userId, String role, Instant occurredAt) {}