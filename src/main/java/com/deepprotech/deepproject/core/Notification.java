package com.deepprotech.deepproject.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String notificationType;
    private String status;
    private String entityType;
    private Long entityId;
}
