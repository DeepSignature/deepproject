package com.deepprotech.deepproject.tasks.dto;

import java.util.UUID;

public record AssigneeCount(UUID assigneeId, String displayName, long count) {}
