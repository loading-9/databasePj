package com.example.controller;

import com.example.dto.*;
import com.example.service.TechnicianService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;


    @PostMapping("/register")
    public ApiResponse<TechnicianDTO> register(@RequestBody TechnicianRegisterRequest request, HttpSession session) {
        return technicianService.registerTechnician(request, session);
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return technicianService.login(request, response);
    }

    @GetMapping("/{technicianId}")
    public ApiResponse<TechnicianDTO> getTechnicianInfo(@PathVariable Long technicianId) {
        return technicianService.getTechnicianInfo(technicianId);
    }

    @PutMapping("/{technicianId}/work-orders/{notificationId}/{workOrderId}/updateStatus")
    public ApiResponse<WorkOrderResponse> updateWorkOrderStatus(
            @PathVariable Long workOrderId,
            @PathVariable Long technicianId,
            @PathVariable Long notificationId,
            @RequestBody Map<String, String> body
    ) {
        String action = body.get("action");
        return technicianService.updateWorkOrderStatus(notificationId, workOrderId, technicianId, action);
    }


    @PutMapping("/{technicianId}/work-orders/{workOrderId}/progress")
    public ApiResponse<WorkOrderResponse> updateProgress(@PathVariable Long workOrderId, @PathVariable Long technicianId,
                                                         @RequestBody ProgressUpdateRequest progressUpdateRequest) {
        return technicianService.updateWorkOrderProgress(workOrderId, technicianId, progressUpdateRequest.progress(), progressUpdateRequest.description());
    }

    @PostMapping("/{technicianId}/work-orders/{workOrderId}/materials")
    public ApiResponse<MaterialDTO> addMaterial(@PathVariable Long workOrderId, @PathVariable Long technicianId, @RequestBody MaterialDTO materialDTO) {
        return technicianService.addMaterial(workOrderId, technicianId, materialDTO);
    }

    @GetMapping("/{technicianId}/work-orders")
    public ApiResponse<List<WorkOrderResponse>> getWorkOrders(@PathVariable Long technicianId) {
        return technicianService.getTechnicianWorkOrders(technicianId);
    }
    @GetMapping("/{technicianId}/repair-records")
    public ApiResponse<List<RepairRecordDTO>> getHistory(@PathVariable Long technicianId) {
        return technicianService.getRepairRecords(technicianId);
    }

    @GetMapping("/{technicianId}/notifications")
    public ApiResponse<List<NotificationDTO>> getNotifications(@PathVariable Long technicianId) {
        return technicianService.getNotifications(technicianId);
    }
}

