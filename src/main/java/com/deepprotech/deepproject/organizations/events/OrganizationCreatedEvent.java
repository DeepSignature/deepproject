package com.deepprotech.deepproject.organizations.events;

import java.time.Instant;
import java.util.UUID;

public record OrganizationCreatedEvent(UUID organizationId, String identifier, String name, UUID createdBy, Instant occurredAt) {}