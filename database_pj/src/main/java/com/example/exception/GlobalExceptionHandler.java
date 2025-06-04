package com.example.exception;

import com.example.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器
 * 集中处理应用中的各类异常，返回统一格式的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 构建错误响应
     *
     * @param message 错误消息
     * @param status  HTTP状态码
     * @return 统一格式的错误响应
     */
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(new ApiResponse<>(status.value(), message, null));
    }

    /**
     * 处理业务逻辑错误(用户名格式错误、用户不存在等)(400)
     *
     * @param ex 无效请求异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(InvalidRequestException ex) {
        logger.warn("无效请求: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    /**
     * 处理参数类型不匹配错误(400)
     *
     * @param ex 参数类型不匹配异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("参数'%s'的类型不正确，应为'%s'",
                ex.getName(), ex.getRequiredType().getSimpleName());
        logger.warn("参数类型不匹配: {}", message);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 安全认证失败(如密码错误)(401)
     *
     * @param ex 认证失败异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("认证失败: {}", ex.getMessage());
        return buildErrorResponse("登录失败: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * 安全认证 用户不存在(400)
     *
     * @param ex 用户不存在异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UsernameNotFoundException ex) {
        logger.warn("用户不存在: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 通用异常处理
     *
     * @param ex 捕获到的异常
     * @return 包含通用错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        // 记录完整异常信息到日志
        logger.error("服务器内部错误", ex);
        // 向客户端返回简化的错误信息，不泄露技术细节
        return buildErrorResponse("服务器内部出错", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理非法参数异常 (400)
     *
     * @param ex 非法参数异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("非法参数: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理空指针异常 (500)
     *
     * @param ex 空指针异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointer(NullPointerException ex) {
        logger.error("空指针异常", ex);
        return buildErrorResponse("服务器处理数据时发生错误", HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
