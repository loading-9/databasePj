package com.example.dto;

import com.example.entity.JobType;

public record TechnicianStatsDTO(
        Long technicianId,
        JobType jobType,
        Long taskCount,
        Double taskPercentage
) {}
