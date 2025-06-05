package com.example.dto.AdvancedStats;

public record TaskStatsDTO(
        String jobType,
        Long completedTasks,
        Long totalTasks,
        Double taskPercentage
) {}