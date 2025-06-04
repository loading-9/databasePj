package com.example.dto;

import java.time.LocalDateTime;

public record FeedbackDTO(
        Long feedbackId,
        Long workOrderId,
        Long userId,
        Integer rating,
        String comment,
        LocalDateTime feedbackTime
) {}