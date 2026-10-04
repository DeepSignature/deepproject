package com.deepprotech.deepproject.organizations.events;

import java.time.Instant;

public record OrganizationMemberAddedEvent(Long organizationId, Long userId, String role, Instant occurredAt) {}