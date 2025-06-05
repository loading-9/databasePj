
package com.example.dto;

import com.example.entity.*;

import java.time.LocalDateTime;

public record RepairRecordDTO(
        Long recordId,
        WorkOrderResponse workOrder,
        TechnicianDTO technician,
        UserDTO user,
        String description,
        LocalDateTime repairTime,
        FeedbackDTO feedback
) {
    // 构造函数，接受实体对象，用于 JPQL
    public RepairRecordDTO(RepairRecord repairRecord, WorkOrder workOrder, Technician technician, User user) {
        this(
                repairRecord.getRecordId(),
                new WorkOrderResponse(workOrder),
                new TechnicianDTO(technician),
                user != null ? new UserDTO(user) : null,
                repairRecord.getDescription(),
                repairRecord.getRepairTime(),
                workOrder.getFeedback() != null ? new FeedbackDTO(workOrder.getFeedback()) : null
        );
    }
}

