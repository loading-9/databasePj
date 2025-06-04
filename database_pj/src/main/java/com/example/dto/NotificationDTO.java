package com.example.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long notificationId,
        Long workOrderId,
        Long userId,
        Long technicianId,
        String message,
        String type,
        LocalDateTime remindTime
) {}
