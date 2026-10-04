package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workspace {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long ownerId;
    private Long organizationId;
}