package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceMember {
    private Long id;
    private Long workspaceId;
    private Long userId;
    @Builder.Default
    private String role = "MEMBER";
}
