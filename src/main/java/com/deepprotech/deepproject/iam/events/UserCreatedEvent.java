package com.deepprotech.deepproject.iam.events;

import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent(UUID userId, String username, String email, Instant occurredAt) {
}