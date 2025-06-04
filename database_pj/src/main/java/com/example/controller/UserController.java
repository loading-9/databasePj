package com.example.controller;

import com.example.dto.*;
import com.example.service.UserService;
import com.example.service.VehicleService;
import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final VehicleService vehicleService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, VehicleService vehicleService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
    }


    @GetMapping("/check-login")
    public ApiResponse<Boolean> checkLoginStatus(@RequestAttribute(required = false) Long userId) {
        if (userId != null) {
            return new ApiResponse<>(HttpStatus.OK.value(), "用户已登录，userId = " + userId, true);
        }
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "用户未登录", false);
    }



    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody RegisterRequest request, HttpSession session) {
        return userService.registerUser(request, session);
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userService.login(request, response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 立即过期
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return new ApiResponse<>(HttpStatus.OK.value(), "已注销登录", null);
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserDTO> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    @GetMapping("/{userId}/vehicles")
    public ApiResponse<List<VehicleResponse>> getUserVehicles(@PathVariable Long userId) {
        return userService.getUserVehicles(userId);
    }

    @GetMapping("/{userId}/work-orders")
    public ApiResponse<List<WorkOrderResponse>> getUserWorkOrders(@PathVariable Long userId) {
        return userService.getUserWorkOrders(userId);
    }

    @PostMapping("/{userId}/submitVehicle")
    public ApiResponse<VehicleResponse> submitVehicle(@PathVariable Long userId, @RequestBody VehicleSubmitRequest vehicleSubmitRequest) {
        return vehicleService.submitVehicle(Math.toIntExact(userId), vehicleSubmitRequest);
    }

    @PostMapping("/{userId}/submitWorkOrder")
    public ApiResponse<WorkOrderResponse> submitWorkOrder(@PathVariable Long userId, @RequestBody WorkOrderSubmitRequest workOrderSubmitRequest) {
        return vehicleService.submitWorkOrder(userId, workOrderSubmitRequest, workOrderSubmitRequest.jobType());
    }

    @PostMapping("/work-orders/{workOrderId}/feedback")
    public ApiResponse<FeedbackDTO> submitFeedback(@PathVariable Long workOrderId, @RequestBody FeedbackDTO feedbackDTO) {
        return userService.submitFeedback(workOrderId, feedbackDTO);
    }

    @PostMapping("/work-orders/{workOrderId}/remind")
    public ApiResponse<NotificationDTO> remindWorkOrder(@PathVariable Long workOrderId, @RequestBody NotificationDTO notificationDTO) {
        return userService.remindWorkOrder(workOrderId, notificationDTO.userId(), notificationDTO.message());
    }

}
