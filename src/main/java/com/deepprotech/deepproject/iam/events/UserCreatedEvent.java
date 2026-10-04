package com.deepprotech.deepproject.iam.events;

import java.time.Instant;

public record UserCreatedEvent(Long userId, String username, String email, Instant occurredAt) {
}