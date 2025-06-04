package com.example.dto;


public record ApiResponse<T>(
        int code,        // 状态码（200/400/500等）
        String message,  // 提示信息
        T data           // 业务数据（泛型，UserDTO或null或其他（如ShopDTO））
) {}
