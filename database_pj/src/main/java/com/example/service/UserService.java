package com.example.service;


import com.beust.ah.A;
import com.example.dto.*;
import com.example.entity.*;
import com.example.exception.InvalidRequestException;
import com.example.repository.*;
import com.example.utils.JwtUtil;
import com.example.utils.PasswordUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final WorkOrderRepository workOrderRepository;
    private final RepairRecordRepository repairRecordRepository;
    private final FeedbackRepository feedbackRepository;
    private final NotificationRepository notificationRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    //注册
    public ApiResponse<UserDTO> registerUser(RegisterRequest request, HttpSession session) {
        // 用户名校验
        if (!request.username().matches("^[a-zA-Z0-9_]{4,20}$")) {
            throw new InvalidRequestException("用户名格式错误!");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new InvalidRequestException("用户名已存在!");
        }

        // 密码校验
        String password = request.password();
        if (password.length() < 6 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            throw new InvalidRequestException("密码需包含字母和数字且至少6位!");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        // user.setPassword(PasswordUtil.encryptPassword(password)); //密码加密
        user.setPassword(password);
        user.setContactInfo(request.contactInfo());
        user.setRegisterTime(LocalDateTime.now());
        user = userRepository.save(user);

        return new ApiResponse<>(HttpStatus.OK.value(), "注册成功!", new UserDTO(user));
    }

    //登录
    public ApiResponse<Void> login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.username());

        if (user == null){
            throw new InvalidRequestException("用户不存在!");
        }

        if (!PasswordUtil.checkPassword(request.password(), user.getPassword())) {
            throw new InvalidRequestException("密码错误!");
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getUserId());

        // 设置 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);


        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 作用于整个后端 API
        cookie.setMaxAge(3600); // 1小时
        cookie.setSecure(false); // 如果部署到 HTTPS，这里要改为 true
        // cookie.setDomain("localhost"); // 可选：设置域名
        response.addCookie(cookie);
        //response.setHeader("Set-Cookie", "token=" + token + "; Path=/; Max-Age=3600; HttpOnly; SameSite=Lax");
        return new ApiResponse<>(HttpStatus.OK.value(), "登录成功!", null);
    }


    public ApiResponse<UserDTO> getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        validateUser(user);
        return new ApiResponse<>(HttpStatus.OK.value(), "查看成功", new UserDTO(user));
    }

    public ApiResponse<List<VehicleResponse>> getUserVehicles(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        validateUser(user);
        List<Vehicle> vehicles = user.getVehicles();
        List<VehicleResponse> vehicleResponses = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            vehicleResponses.add(new VehicleResponse(vehicle));
        }
        return new ApiResponse<>(HttpStatus.OK.value(), "查看成功", vehicleResponses);
    }

    public ApiResponse<List<WorkOrderResponse>> getUserWorkOrders(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        validateUser(user);
        List<WorkOrder> workOrders = user.getWorkOrders();
        List<WorkOrderResponse> workOrderResponses = new ArrayList<>();
        for (WorkOrder workOrder : workOrders) {
            workOrderResponses.add(new WorkOrderResponse(workOrder));
        }
        return new ApiResponse<>(HttpStatus.OK.value(), "工单查看成功", workOrderResponses);
    }

    @Transactional
    public ApiResponse<FeedbackDTO> submitFeedback(Long workOrderId, FeedbackDTO feedbackDTO) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        if (workOrder == null || !workOrder.getStatus().equals(WorkOrderStatus.已完成)) {
            return new ApiResponse<>(400, "工单未完成或不存在", null);
        }
        if (feedbackRepository.existsByWorkOrder(workOrder)) {
            return new ApiResponse<>(400, "工单已存在反馈", null);
        }
        if (!userRepository.existsById(feedbackDTO.userId())) {
            throw new InvalidRequestException("用户不存在");
        }
        Feedback feedback = new Feedback();
        feedback.setWorkOrder(workOrder);
        feedback.setUser(userRepository.findById(feedbackDTO.userId()).orElse(null));
        feedback.setRating(feedbackDTO.rating());
        feedback.setComment(feedbackDTO.comment());
        feedback.setFeedbackTime(LocalDateTime.now());
        feedbackRepository.save(feedback);
        return new ApiResponse<>(201, "反馈提交成功", feedbackDTO);
    }

    @Transactional
    public ApiResponse<NotificationDTO> remindWorkOrder(Long workOrderId, Long userId, String message) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (workOrder == null) {
            return new ApiResponse<>(400, "工单不存在", null);
        }
        if (!workOrder.getUser().getUserId().equals(userId)) {
            return new ApiResponse<>(400, "用户无权限催促此工单", null);
        }
        if (workOrder.getStatus().equals(WorkOrderStatus.已完成)) {
            return new ApiResponse<>(400, "工单已完成，无法催单", null);
        }
        Notification notification = new Notification();
        notification.setWorkOrderId(workOrderId);
        notification.setUserId(userId);
        notification.setTechnicianId(workOrder.getTechnician().getTechnicianId());
        notification.setMessage(message != null && !message.isEmpty() ? message : "请尽快处理工单");
        notification.setRemindTime(LocalDateTime.now());
        notification.setType("bool");
        notificationRepository.save(notification);
        return new ApiResponse<>(201, "催单成功", new NotificationDTO(
                notification.getNotificationId(),
                notification.getWorkOrderId(),
                notification.getUserId(),
                notification.getTechnicianId(),
                notification.getMessage(),
                notification.getType(),
                notification.getRemindTime()
        ));
    }

    public ApiResponse<List<RepairRecordDTO>> getUserRepairRecords(Long userId) {
        List<WorkOrder> workOrders = workOrderRepository.findByUserId(userId);
        List<RepairRecordDTO> recordDTOs = new ArrayList<>();
        for (WorkOrder workOrder : workOrders) {
            RepairRecord repairRecord = repairRecordRepository.findByWorkOrder(workOrder);
            if (repairRecord != null) {
                recordDTOs.add(new RepairRecordDTO(
                        repairRecord,
                        workOrder,
                        repairRecord.getTechnician(),
                        workOrder.getUser()
                ));
            }
        }
        return new ApiResponse<>(200, "维修记录查询成功", recordDTOs);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidRequestException("不存在该用户");
        }
    }

}
