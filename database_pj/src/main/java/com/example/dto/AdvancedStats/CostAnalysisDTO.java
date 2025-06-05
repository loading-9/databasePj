package com.example.dto.AdvancedStats;

import java.math.BigDecimal;

public record CostAnalysisDTO(
        String period, // 年份-季度 或 年份-月份
        BigDecimal laborCost,
        BigDecimal materialCost,
        BigDecimal totalCost,
        Double laborCostPercentage,
        Double materialCostPercentage
) {}