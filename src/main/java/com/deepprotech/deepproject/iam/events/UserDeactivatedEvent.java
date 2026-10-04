package com.deepprotech.deepproject.iam.events;

import java.time.Instant;

public record UserDeactivatedEvent(Long userId, String username, Instant occurredAt) {
}