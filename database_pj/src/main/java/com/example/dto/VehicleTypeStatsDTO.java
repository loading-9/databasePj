package com.example.dto;

public record VehicleTypeStatsDTO(
        String vehicleType,
        Long repairCount,
        Double avgCost
) {}