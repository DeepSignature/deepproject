package com.deepprotech.deepproject.tasks.dto;

import com.deepprotech.deepproject.common.dto.CursorPage;

import java.util.List;
import java.util.UUID;

public record AssigneeDashboardResponse(
        UUID assigneeId,
        String displayName,
        long totalTaskCount,
        long overdueTaskCount,
        long completedTaskCount,
        double completionPercentage,
        List<StatusCount> tasksByStatus,
        List<PriorityCount> tasksByPriority,
        CursorPage<TaskResponse> tasks
) {}
