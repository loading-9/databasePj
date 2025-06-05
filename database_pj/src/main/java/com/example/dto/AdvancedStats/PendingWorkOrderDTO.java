package com.example.dto.AdvancedStats;

import com.example.entity.JobType;

public record PendingWorkOrderDTO(
        Long workOrderId,
        String vehicleType,
        String problem,
        JobType jobType,
        Long technicianId,
        String technicianName
) {}
