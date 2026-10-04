package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String identityId;
    private String username;
    private String email;
    private String displayName;
    @Builder.Default
    private boolean active = true;
}