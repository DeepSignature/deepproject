package com.deepprotech.deepproject.tasks.dto;

import java.math.BigDecimal;
import java.util.List;

public record TaskPeriodStatistics(
        long createdTasks,
        long completedTasks,
        long overdueTasks,
        double completionRate,
        Double avgCycleTimeSeconds,
        BigDecimal estimatedHours,
        BigDecimal actualHours,
        List<StatusCount> byStatus,
        List<PriorityCount> byPriority,
        List<TypeCount> byType
) {}
