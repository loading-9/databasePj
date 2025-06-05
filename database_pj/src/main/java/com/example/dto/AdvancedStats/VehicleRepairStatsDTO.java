package com.example.dto.AdvancedStats;

import java.math.BigDecimal;

public record VehicleRepairStatsDTO(
        String vehicleType,
        Long repairCount,
        BigDecimal averageCost,
        String mostCommonProblem
) {}