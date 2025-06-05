package com.example.dto;

import com.example.entity.Feedback;

import java.time.LocalDateTime;




public record FeedbackDTO(
        Long feedbackId,
        Long userId,
        Integer rating,
        String comment,
        LocalDateTime feedbackTime
) {
    public FeedbackDTO(Feedback feedback) {
        this(
                feedback.getFeedbackId(),
                0L,
                feedback.getRating(),
                feedback.getComment(),
                feedback.getFeedbackTime()
        );
    }
}
