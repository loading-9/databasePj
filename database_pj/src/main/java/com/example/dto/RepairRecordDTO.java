package com.example.dto;

import java.time.LocalDateTime;

public record RepairRecordDTO(
        Long recordId,
        Long workOrderId,
        Long technicianId,
        String description,
        LocalDateTime repairTime
) {}
