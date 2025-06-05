package com.example.dto.AdvancedStats;

public record NegativeFeedbackDTO(
        Long workOrderId,
        Long feedbackId,
        Integer rating,
        String comment,
        Long technicianId,
        String technicianName
) {}