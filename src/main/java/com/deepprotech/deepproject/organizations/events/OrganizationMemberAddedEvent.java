package com.deepprotech.deepproject.organizations.events;

import java.time.Instant;
import java.util.UUID;

public record OrganizationMemberAddedEvent(UUID organizationId, UUID userId, String role, Instant occurredAt) {}