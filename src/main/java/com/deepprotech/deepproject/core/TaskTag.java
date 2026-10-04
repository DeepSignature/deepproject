package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTag {
    private Long id;
    private Long taskId;
    private String tagName;
}
