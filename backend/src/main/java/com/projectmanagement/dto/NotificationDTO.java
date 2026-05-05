package com.projectmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private Long recipientId;
    private String recipientName;
    private String message;
    private String senderName;
    private boolean read;
    private LocalDateTime createdAt;
}
