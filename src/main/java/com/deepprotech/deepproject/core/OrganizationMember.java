package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationMember {
    private Long id;
    private Long organizationId;
    private Long userId;
    private String role;
}