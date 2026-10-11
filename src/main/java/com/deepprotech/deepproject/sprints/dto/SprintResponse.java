package com.deepprotech.deepproject.sprints.dto;

import com.deepprotech.deepproject.core.Sprint;

import java.time.Instant;
import java.util.UUID;

public record SprintResponse(
        UUID id,
        UUID projectId,
        String name,
        String description,
        boolean active,
        String status,
        Instant startDate,
        Instant endDate
) {
    public static SprintResponse from(Sprint s) {
        return new SprintResponse(
                s.getId(),
                s.getProjectId(),
                s.getName(),
                s.getDescription(),
                s.isActive(),
                s.getStatus(),
                s.getStartDate(),
                s.getEndDate()
        );
    }
}
