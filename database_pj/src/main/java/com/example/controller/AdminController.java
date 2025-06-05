
package com.example.controller;

import com.example.dto.*;
import com.example.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return new ApiResponse<>(200, "用户列表获取成功", users);
    }

    @GetMapping("/technicians")
    public ApiResponse<List<TechnicianDTO>> getAllTechnicians() {
        List<TechnicianDTO> technicians = adminService.getAllTechnicians();
        return new ApiResponse<>(200, "技师列表获取成功", technicians);
    }

    @GetMapping("/vehicles")
    public ApiResponse<List<VehicleResponse>> getAllVehicles() {
        List<VehicleResponse> vehicles = adminService.getAllVehicles();
        return new ApiResponse<>(200, "车辆列表获取成功", vehicles);
    }

    @GetMapping("/work-orders")
    public ApiResponse<List<WorkOrderResponse>> getAllWorkOrders() {
        List<WorkOrderResponse> workOrders = adminService.getAllWorkOrders();
        return new ApiResponse<>(200, "工单列表获取成功", workOrders);
    }

    @GetMapping("/repair-histories")
    public ApiResponse<List<RepairRecordDTO>> getAllRepairHistories() {
        List<RepairRecordDTO> repairHistories = adminService.getAllRepairHistories();
        return new ApiResponse<>(200, "维修记录列表获取成功", repairHistories);
    }
}

