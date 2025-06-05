package com.example.dto;

import com.example.entity.Feedback;
import com.example.entity.WorkOrder;
import com.example.entity.WorkOrderStatus;

import java.time.LocalDateTime;

public record WorkOrderResponse(Long workOrderId, VehicleResponse vehicleResponse, WorkOrderStatus workOrderStatus, TechnicianDTO technicianDto, String problem, LocalDateTime submitTime, double progress, String progressDescription, Double laborCost, Double materialCost, Double totalCost) {
    public WorkOrderResponse(WorkOrder workOrder) {
        this(
                workOrder.getWorkOrderId(),
                workOrder.getVehicle() != null ? new VehicleResponse(workOrder.getVehicle()) : null,
                workOrder.getStatus(),
                workOrder.getTechnician() != null ? new TechnicianDTO(workOrder.getTechnician()) : null,
                workOrder.getProblem(),
                workOrder.getSubmitTime(),
                workOrder.getProgress(),
                workOrder.getProgressDescription(),
                workOrder.getLaborCost(),
                workOrder.getMaterialCost(),
                workOrder.getTotalCost()
        );
    }
}
