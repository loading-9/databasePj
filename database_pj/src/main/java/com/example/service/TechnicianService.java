package com.example.service;

import com.example.dto.*;
import com.example.entity.*;
import com.example.exception.InvalidRequestException;
import com.example.repository.*;
import com.example.utils.JwtUtil;
import com.example.utils.PasswordUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TechnicianService {
    @Autowired
    private TechnicianRepository technicianRepository;
    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private RepairRecordRepository repairRecordRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private VehicleService vehicleService;

    public ApiResponse<TechnicianDTO> registerTechnician(TechnicianRegisterRequest request, HttpSession session) {
        // 用户名校验
        if (!request.username().matches("^[a-zA-Z0-9_]{4,20}$")) {
            throw new InvalidRequestException("用户名格式错误!");
        }

        if (technicianRepository.existsByUsername(request.username())) {
            throw new InvalidRequestException("用户名已存在!");
        }

        // 密码校验
        String password = request.password();
        if (password.length() < 6 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            throw new InvalidRequestException("密码需包含字母和数字且至少6位!");
        }

        // 创建用户
        Technician technician = new Technician();
        technician.setUsername(request.username());
        technician.setName(request.name());
        technician.setPassword(PasswordUtil.encryptPassword(password)); //密码加密
        technician.setContactInfo(request.contactInfo());
        technician.setJobType(request.jobType());
        technician.setHourlyRate(request.hourlyRate());
        technician.setIncome(request.income());
        technician = technicianRepository.save(technician);

        return new ApiResponse<>(HttpStatus.OK.value(), "注册成功!", new TechnicianDTO(technician));
    }

    //登录
    public ApiResponse<Void> login(LoginRequest request, HttpServletResponse response) {
        Technician technician = technicianRepository.findByUsername(request.username()).orElse(null);
        if (technician == null){
            throw new InvalidRequestException("用户不存在!");
        }
        if (!PasswordUtil.checkPassword(request.password(), technician.getPassword())) {
            throw new BadCredentialsException("密码错误!");
        }

        String token = JwtUtil.generateToken(technician.getUsername(), technician.getTechnicianId());
        // 设置 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 作用于整个后端 API
        cookie.setMaxAge(3600); // 1小时
        cookie.setSecure(false); // 如果部署到 HTTPS，这里要改为 true
        response.addCookie(cookie);
        //response.setHeader("Set-Cookie", "token=" + token + "; Path=/; Max-Age=3600; HttpOnly; SameSite=Lax");
        return new ApiResponse<>(HttpStatus.OK.value(), "登录成功!", null);
    }

    public ApiResponse<TechnicianDTO> getTechnicianInfo(Long technicianId) {
        Technician technician = technicianRepository.findById(technicianId).orElse(null);
        if (technician == null) {
            throw new InvalidRequestException("维修人员不存在");
        }
        return new ApiResponse<>(200, "查询成功", new TechnicianDTO(technician.getTechnicianId(), technician.getUsername(), technician.getName(), technician.getJobType(), technician.getHourlyRate(), technician.getContactInfo(), technician.getIncome()));
    }

    @Transactional
    public ApiResponse<WorkOrderResponse> updateWorkOrderStatus(Long notificationId, Long workOrderId, Long technicianId, String action) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        if (workOrder == null) {
            return new ApiResponse<>(400, "工单不存在", null);
        }
        if (!technicianRepository.existsById(technicianId)) {
            return new ApiResponse<>(400, "维修人员不存在", null);
        }
        if (action.equals("accept")) {
            if (workOrder.getTechnician() != null) {
                return new ApiResponse<>(400, "工单已分配", null);
            }
            Technician technician = technicianRepository.findById(technicianId).orElse(null);
            workOrder.setTechnician(technician);
            workOrder.setStatus(WorkOrderStatus.进行中);
            notificationRepository.removeNotificationByNotificationId(notificationId);
        } else if (action.equals("reject")) {
            Technician technician = technicianRepository.findById(technicianId).orElse(null);
            workOrder.setTechnician(null);
            workOrder.setStatus(WorkOrderStatus.待分配);
            notificationRepository.removeNotificationByNotificationId(notificationId);
            if (technician != null) {
                vehicleService.assignWorkOrder(workOrder.getUser().getUserId(), workOrderId, technician.getJobType(), technicianId);
            }
        } else if (action.equals("read already")){
            notificationRepository.removeNotificationByNotificationId(notificationId);
        } else {
            return new ApiResponse<>(400, "无效操作", null);
        }
        workOrderRepository.save(workOrder);
        return new ApiResponse<>(200, "状态更新成功", new WorkOrderResponse(workOrder));
    }

    @Transactional
    public ApiResponse<WorkOrderResponse> updateWorkOrderProgress(Long workOrderId, Long technicianId, Double progress, String description) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        Technician technician = technicianRepository.findById(technicianId).orElse(null);
        if (workOrder == null || !Objects.equals(workOrder.getTechnician().getTechnicianId(), technicianId)) {
            return new ApiResponse<>(400, "工单未分配给该维修人员或不存在", null);
        }
        workOrder.setProgress(progress);
        if (progress >= 1) {
            workOrder.setStatus(WorkOrderStatus.已完成);
            workOrder.setCompleteTime(LocalDateTime.now());
            workOrderRepository.save(workOrder);
            RepairRecord record = new RepairRecord();
            record.setWorkOrder(workOrder);
            record.setTechnician(technician);
            record.setDescription(description);
            record.setRepairTime(LocalDateTime.now());
            repairRecordRepository.save(record);
        }

        return new ApiResponse<>(200, "进度更新成功", new WorkOrderResponse(workOrder));
    }

    @Transactional
    public ApiResponse<MaterialDTO> addMaterial(Long workOrderId, Long technicianId, MaterialDTO materialDTO) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        if (workOrder == null || !Objects.equals(workOrder.getTechnician().getTechnicianId(), technicianId)) {
            return new ApiResponse<>(400, "工单未分配给该维修人员或不存在", null);
        }
        Material material = new Material();
        material.setWorkOrder(workOrder);
        material.setMaterialName(materialDTO.materialName());
        material.setQuantity(materialDTO.quantity());
        material.setUnitPrice(materialDTO.unitPrice());
        material.setTotalPrice(materialDTO.quantity() * materialDTO.unitPrice());
        materialRepository.save(material);
        return new ApiResponse<>(201, "材料记录成功", new MaterialDTO(material.getMaterialId(), workOrderId, material.getMaterialName(), material.getQuantity(), material.getUnitPrice(), material.getTotalPrice()));
    }

    public ApiResponse<List<RepairRecordDTO>> getRepairRecords(Long technicianId) {
        if (!technicianRepository.existsById(technicianId)) {
            return new ApiResponse<>(404, "维修人员不存在", null);
        }
        Technician technician = technicianRepository.findById(technicianId).orElse(null);
        List<RepairRecord> records = new ArrayList<>();
        if (technician != null) {
            records = technician.getRepairRecords();
        }
        List<RepairRecordDTO> recordDTOs = records.stream().map(r -> new RepairRecordDTO(r.getRecordId(), r.getWorkOrder().getWorkOrderId(), r.getTechnician().getTechnicianId(), r.getDescription(), r.getRepairTime())).collect(Collectors.toList());
        return new ApiResponse<>(200, "查询成功", recordDTOs);
    }

    public ApiResponse<List<NotificationDTO>> getNotifications(Long technicianId) {
        if (!technicianRepository.existsById(technicianId)) {
            return new ApiResponse<>(404, "维修人员不存在", null);
        }
        List<Notification> notifications = notificationRepository.findByTechnicianId(technicianId);
        List<NotificationDTO> notificationDTOs = notifications.stream().map(n -> new NotificationDTO(
                n.getNotificationId(),
                n.getWorkOrderId(),
                n.getUserId(),
                n.getTechnicianId(),
                n.getMessage(),
                n.getType(),
                n.getRemindTime()
        )).collect(Collectors.toList());
        return new ApiResponse<>(200, "查询成功", notificationDTOs);
    }
}

