package com.deepprotech.deepproject.tasks.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectStatisticsResponse(
        UUID projectId,
        Instant from,
        Instant to,
        Instant previousFrom,
        Instant previousTo,
        TaskPeriodStatistics current,
        TaskPeriodStatistics previous
) {}
