package com.deepprotech.deepproject.tasks.dto;

import java.time.Instant;

public interface TaskCycleTime {
    Instant getCreatedAt();
    Instant getCompletedAt();
}
