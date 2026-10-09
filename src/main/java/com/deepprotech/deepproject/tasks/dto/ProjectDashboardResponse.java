package com.deepprotech.deepproject.tasks.dto;

import com.deepprotech.deepproject.common.dto.CursorPage;

import java.util.List;

public record ProjectDashboardResponse(
        long totalTaskCount,
        long overdueTaskCount,
        long completedTaskCount,
        double completionPercentage,
        List<StatusCount> tasksByStatus,
        List<PriorityCount> tasksByPriority,
        CursorPage<TaskResponse> tasks
) {}
