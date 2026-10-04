package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    private Long id;
    private Long workspaceId;
    private String name;
    private String description;
    @Builder.Default
    private String status = "ACTIVE";
}
