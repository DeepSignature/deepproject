package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    private Long id;
    private Long projectId;
    private Long parentTaskId;
    private String title;
    private String description;
    @Builder.Default
    private String status = "TODO";
    @Builder.Default
    private String priority = "MEDIUM";
    @Builder.Default
    private String taskType = "TASK";
}
