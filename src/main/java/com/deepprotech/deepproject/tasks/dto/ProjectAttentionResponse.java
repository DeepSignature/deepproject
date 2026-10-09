package com.deepprotech.deepproject.tasks.dto;

import java.util.List;
import java.util.UUID;

public record ProjectAttentionResponse(
        UUID projectId,
        List<TaskResponse> overdueTasks,
        List<TaskResponse> highPriorityTasks,
        List<TaskResponse> staleTasks
) {}
