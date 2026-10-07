package com.deepprotech.deepproject.tasks.dto;

import com.deepprotech.deepproject.core.Task;

import java.util.UUID;

public record TaskResponse(UUID id, UUID projectId, UUID parentTaskId, String title, String description,
                           String status, String priority, String taskType) {
    public static TaskResponse from(Task t) {
        return new TaskResponse(t.getId(), t.getProjectId(), t.getParentTaskId(),
                t.getTitle(), t.getDescription(), t.getStatus(), t.getPriority(), t.getTaskType());
    }
}
