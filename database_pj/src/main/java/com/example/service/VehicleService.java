package com.example.service;


import com.example.dto.*;
import com.example.entity.*;
import com.example.exception.InvalidRequestException;
import com.example.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final WorkOrderRepository workOrderRepository;
    private final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    private final WorkOrderTechnicianRepository workOrderTechnicianRepository;
    private final NotificationRepository notificationRepository;
    private final TechnicianRepository technicianRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public ApiResponse<VehicleResponse> submitVehicle(Long userId, VehicleSubmitRequest request) {
        logger.info("Received submitVehicle request for userId: {}, request: {}", userId, request);

        try {
            // 检查用户是否存在
            logger.debug("Looking up user with ID: {}", userId);
            User user = userRepository.findById((long) userId).orElse(null);
            if (user == null) {
                logger.warn("No user found for userId: {}", userId);
                return new ApiResponse<>(404, "User not found for userId: " + userId, null);
            }

            // 调用原生 SQL 插入车辆记录
            logger.debug("Inserting vehicle record via SQL");
            vehicleRepository.insertVehicle(
                    (long) userId,
                    request.licencePlate(),
                    request.vehicleType(),
                    request.brand(),
                    request.manufactureYear()
            );

            // 获取刚插入的车辆ID
            Long vehicleId = vehicleRepository.getLastInsertedVehicleId();

            // 构造响应数据
            VehicleResponse response = new VehicleResponse(
                    vehicleId,
                    request.licencePlate(),
                    request.vehicleType(),
                    request.brand(),
                    request.manufactureYear(),
                    userId
            );

            logger.info("Vehicle submitted successfully with ID: {}", vehicleId);
            return new ApiResponse<>(200, "车辆添加成功", response);

        } catch (Exception e) {
            logger.error("Failed to submit vehicle for userId: {}. Error: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(500, "Failed to submit vehicle: " + e.getMessage(), null);
        }
    }


    @Transactional
    public ApiResponse<WorkOrderResponse> submitWorkOrder(Long userId, WorkOrderSubmitRequest request, JobType jobType) {
        logger.info("Received submitWorkOrder request for userId: {}, request: {}", userId, request);

        try {
            // 检查用户是否存在
            logger.debug("Looking up user with ID: {}", userId);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                logger.warn("No user found for userId: {}", userId);
                return new ApiResponse<>(404, "User not found for userId: " + userId, null);
            }

            // 检查车辆是否存在且属于该用户
            logger.debug("Looking up vehicle with ID: {}", request.vehicleId());
            Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElse(null);
            if (vehicle == null) {
                logger.warn("No vehicle found for vehicleId: {}", request.vehicleId());
                return new ApiResponse<>(404, "Vehicle not found for vehicleId: " + request.vehicleId(), null);
            }

            if (jobType == null) {
                return new ApiResponse<>(404, "Please submit the jobtype of technician you want. " + request.vehicleId(), null);
            }

            // 调用原生 SQL 插入工单记录
            logger.debug("Inserting work order record via SQL");
            LocalDateTime submitTime = LocalDateTime.now();
            workOrderRepository.insertWorkOrder(
                    userId,
                    request.vehicleId(),
                    request.problem(),
                    "待分配",
                    submitTime,
                    0.0
            );

            // 获取刚插入的工单ID
            Long workOrderId = workOrderRepository.getLastInsertedWorkOrderId();

            // 构造响应数据
            WorkOrderResponse response = new WorkOrderResponse(
                    Objects.requireNonNull(workOrderRepository.findById(workOrderId).orElse(null))
            );

            assignWorkOrder(workOrderId, workOrderId, jobType, 0L);

            logger.info("Work order submitted successfully with ID: {}", workOrderId);
            return new ApiResponse<>(200, "工单提交成功", response);

        } catch (Exception e) {
            logger.error("Failed to submit work order for userId: {}. Error: {}", userId, e.getMessage(), e);
            return new ApiResponse<>(500, "Failed to submit work order: " + e.getMessage(), null);
        }
    }

    public boolean assignWorkOrder(Long userId, Long workOrderId, JobType jobType, Long technicianId) {
        List<Technician> technicians = technicianRepository.findByJobTypeAndTechnicianIdGreaterThan(jobType, technicianId);
        if (!technicians.isEmpty()) {
            technicianId = technicians.getFirst().getTechnicianId();
            WorkOrderTechnician candidate = new WorkOrderTechnician();
            candidate.setWorkOrderId(workOrderId);
            candidate.setTechnicianId(technicianId);
            workOrderTechnicianRepository.save(candidate);

            // 发送候选通知
            Notification notification = new Notification();
            notification.setWorkOrderId(workOrderId);
            notification.setUserId(userId);
            notification.setTechnicianId(technicianId);
            notification.setMessage("您被选为工单候选人，请确认接受或拒绝");
            notification.setRemindTime(LocalDateTime.now());
            notification.setType("choose");
            notificationRepository.save(notification);
            return true;
        } else {
            return false;
        }
    }

    public ApiResponse<FeedbackDTO> getFeedBack(Long workOrdersId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrdersId).orElse(null);
        Feedback feedback = feedbackRepository.findByWorkOrder(workOrder);
        if (feedback == null) {
            throw new InvalidRequestException("No Feedback found for work order: " + workOrdersId);
        }
        FeedbackDTO feedbackDTO = new FeedbackDTO(feedback);
        return new ApiResponse<>(200, "查询成功", feedbackDTO);
    }
}
