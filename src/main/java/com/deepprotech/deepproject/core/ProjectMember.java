package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMember {
    private Long id;
    private Long projectId;
    private Long userId;
    @Builder.Default
    private String role = "MEMBER";
}
