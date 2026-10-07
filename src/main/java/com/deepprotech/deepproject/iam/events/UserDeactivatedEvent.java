package com.deepprotech.deepproject.iam.events;

import java.time.Instant;
import java.util.UUID;

public record UserDeactivatedEvent(UUID userId, String username, Instant occurredAt) {
}