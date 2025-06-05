package com.example.service;

import com.example.dto.*;
import com.example.dto.AdvancedStats.*;
import com.example.entity.*;
import com.example.exception.InvalidRequestException;
import com.example.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final VehicleRepository vehicleRepository;
    private final WorkOrderRepository workOrderRepository;
    private final RepairRecordRepository repairRecordRepository;

    public AdminService(UserRepository userRepository,
                        TechnicianRepository technicianRepository,
                        VehicleRepository vehicleRepository,
                        WorkOrderRepository workOrderRepository,
                        RepairRecordRepository repairRecordRepository) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.vehicleRepository = vehicleRepository;
        this.workOrderRepository = workOrderRepository;
        this.repairRecordRepository = repairRecordRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsersSummary();
    }

    public List<TechnicianDTO> getAllTechnicians() {
        return technicianRepository.findAllTechniciansSummary()
                .stream()
                .map(TechnicianDTO::new)
                .collect(Collectors.toList());
    }


    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAllVehiclesSummary();
    }

    public List<WorkOrderResponse> getAllWorkOrders() {
        return workOrderRepository.findAllWorkOrders()
                .stream()
                .map(WorkOrderResponse::new)
                .toList();

    }

    public List<RepairRecordDTO> getAllRepairHistories() {
        return repairRecordRepository.findAllRepairHistories();
    }

    @Autowired
    private OperationLogRepository operationLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* // 统计车型维修次数与平均费用
    public ApiResponse<List<VehicleRepairStatsDTO>> getVehicleRepairStats() {
        List<VehicleRepairStatsDTO> stats = workOrderRepository.findVehicleRepairStats();
        return new ApiResponse<>(200, "车型维修统计查询成功", stats);
    }

    // 统计特定车型维修信息
    public ApiResponse<VehicleRepairStatsDTO> getVehicleRepairStatsByType(String vehicleType) {
        VehicleRepairStatsDTO stats = workOrderRepository.findVehicleRepairStatsByType(vehicleType);
        if (stats == null) {
            return new ApiResponse<>(404, "未找到该车型的维修记录", null);
        }
        return new ApiResponse<>(200, "车型维修统计查询成功", stats);
    } */

    // 按月统计维修费用构成
    /* public ApiResponse<List<CostAnalysisDTO>> getCostAnalysisByMonth(LocalDateTime startTime, LocalDateTime endTime) {
        List<CostAnalysisDTO> analysis = workOrderRepository.findCostAnalysisByMonth(startTime, endTime);
        return new ApiResponse<>(200, "费用分析查询成功", analysis);
    }

    // 筛选负面反馈工单
    public ApiResponse<List<NegativeFeedbackDTO>> getNegativeFeedbackWorkOrders() {
        List<NegativeFeedbackDTO> feedbacks = workOrderRepository.findNegativeFeedbackWorkOrders();
        return new ApiResponse<>(200, "负面反馈查询成功", feedbacks);
    }

    // 统计工种任务数量与占比
    public ApiResponse<List<TaskStatsDTO>> getTaskStatsByJobType() {
        List<TaskStatsDTO> stats = workOrderRepository.findTaskStatsByJobType();
        return new ApiResponse<>(200, "工种任务统计查询成功", stats);
    }

    // 统计未完成工单
    public ApiResponse<List<PendingWorkOrderDTO>> getPendingWorkOrders() {
        List<PendingWorkOrderDTO> pendingWorkOrders = workOrderRepository.findPendingWorkOrders();
        return new ApiResponse<>(200, "未完成工单查询成功", pendingWorkOrders);
    }

    // 批量提交工单与技师分配
    /* @Transactional
    public ApiResponse<List<WorkOrderResponse>> batchCreateWorkOrders(List<WorkOrderSubmitRequest> requests, Long adminId) {
        List<WorkOrderResponse> responses = new ArrayList<>();
        for (WorkOrderSubmitRequest request : requests) {
            WorkOrder workOrder = new WorkOrder();
            User user = userRepository.findById(request.userId()).orElse(null);
            if (user == null) {
                throw new InvalidRequestException("用户ID " + request.userId() + " 不存在");
            }
            Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElse(null);
            if (vehicle == null) {
                throw new InvalidRequestException("车辆ID " + request.vehicleId() + " 不存在");
            }
            workOrder.setUser(user);
            workOrder.setVehicle(vehicle);
            workOrder.setProblem(request.problem());
            workOrder.setStatus(WorkOrderStatus.待分配);
            workOrder.setSubmitTime(LocalDateTime.now());
            workOrder.setProgress(0.0);
            workOrderRepository.save(workOrder);
            logOperation("WorkOrder", workOrder.getWorkOrderId(), "INSERT", null, workOrder, adminId);

            // 分配技师
            if (request.technicianId() != null) {
                Technician technician = technicianRepository.findById(request.technicianId()).orElse(null);
                if (technician == null) {
                    throw new InvalidRequestException("技师ID " + request.technicianId() + " 不存在");
                }
                workOrder.setTechnician(technician);
                workOrder.setStatus(WorkOrderStatus.进行中);
                workOrderRepository.save(workOrder);
                logOperation("WorkOrder", workOrder.getWorkOrderId(), "UPDATE", workOrder, workOrder, adminId);
            }

            responses.add(new WorkOrderResponse(workOrder));
        }
        return new ApiResponse<>(200, "批量创建工单成功", responses);
    }

    // 批量结算技师月度工时费
    @Transactional
    public ApiResponse<List<TechnicianIncomeDTO>> settleMonthlyIncome(LocalDateTime startTime, LocalDateTime endTime, Long adminId) {
        List<Technician> technicians = technicianRepository.findAll();
        List<TechnicianIncomeDTO> incomes = new ArrayList<>();
        for (Technician technician : technicians) {
            List<WorkOrder> workOrders = workOrderRepository.findByTechnician(technician).stream()
                    .filter(w -> w.getCompleteTime() != null &&
                            w.getCompleteTime().isAfter(startTime) &&
                            w.getCompleteTime().isBefore(endTime))
                    .toList();
            double totalLaborCost = workOrders.stream()
                    .mapToDouble(WorkOrder::getLaborCost)
                    .sum();
            double totalWorkHours = workOrders.size() * 1.0; // 假设每工单1小时
            double currentIncome = technician.getIncome();
            technician.setIncome(currentIncome + totalLaborCost);
            technicianRepository.save(technician);
            logOperation("Technician", technician.getTechnicianId(), "UPDATE", null, technician, adminId);
            incomes.add(new TechnicianIncomeDTO(technician.getTechnicianId(), technician.getName(), totalWorkHours, totalLaborCost));
        }
        return new ApiResponse<>(200, "月度工时费结算成功", incomes);
    }

    // 更新技师信息并同步历史记录
    @Transactional
    public ApiResponse<TechnicianDTO> updateTechnicianInfo(Long technicianId, TechnicianUpdateRequest request, Long adminId) {
        Technician technician = technicianRepository.findById(technicianId).orElse(null);
        if (technician == null) {
            throw new InvalidRequestException("维修人员不存在");
        }
        Technician oldTechnician = copyTechnician(technician);
        technician.setName(request.name());
        technician.setContactInfo(request.contactInfo());
        technician.setJobType(request.jobType());
        technician.setHourlyRate(request.hourlyRate());
        technicianRepository.save(technician);
        logOperation("Technician", technicianId, "UPDATE", oldTechnician, technician, adminId);

        // 同步更新维修记录（仅更新 technicianDTO）
        List<RepairRecord> records = repairRecordRepository.findAll().stream()
                .filter(r -> r.getTechnician().getTechnicianId().equals(technicianId))
                .toList();
        for (RepairRecord record : records) {
            // RepairRecordDTO 会自动使用更新后的 Technician
            logOperation("RepairRecord", record.getRecordId(), "UPDATE", record, record, adminId);
        }
        return new ApiResponse<>(200, "技师信息更新成功", new TechnicianDTO(technician));
    }

    // 删除工单并级联删除
    @Transactional
    public ApiResponse<Void> deleteWorkOrder(Long workOrderId, Long adminId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        if (workOrder == null) {
            return new ApiResponse<>(404, "工单不存在", null);
        }
        logOperation("WorkOrder", workOrderId, "DELETE", workOrder, null, adminId);
        workOrderRepository.delete(workOrder);
        return new ApiResponse<>(200, "工单删除成功", null);
    }

    // 工具方法：复制 Technician
    private Technician copyTechnician(Technician technician) {
        Technician copy = new Technician();
        copy.setTechnicianId(technician.getTechnicianId());
        copy.setUsername(technician.getUsername());
        copy.setName(technician.getName());
        copy.setPassword(technician.getPassword());
        copy.setContactInfo(technician.getContactInfo());
        copy.setJobType(technician.getJobType());
        copy.setHourlyRate(technician.getHourlyRate());
        copy.setIncome(technician.getIncome());
        return copy;
    }

    // 工具方法：记录操作日志
    private void logOperation(String entityType, Long entityId, String operation, Object oldData, Object newData, Long operatedBy) {
        try {
            OperationLog log = new OperationLog();
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setOperation(operation);
            log.setOldData(oldData != null ? objectMapper.writeValueAsString(oldData) : null);
            log.setNewData(newData != null ? objectMapper.writeValueAsString(newData) : null);
            log.setOperationTime(LocalDateTime.now());
            log.setOperatedBy(operatedBy);
            operationLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to log operation: " + e.getMessage());
        }
    } */
}
