package com.deepprotech.deepproject.organizations.events;

import java.time.Instant;

public record OrganizationCreatedEvent(Long organizationId, String identifier, String name, Long createdBy, Instant occurredAt) {}