package com.liyuxiang.common.handler;

import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.exception.BusinessException;
import com.liyuxiang.common.result.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

/**
 * @Author: liyuxiang
 * @Date: 2025/9/22 15:53
 * @Description: 全局异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseResult<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        return ResponseResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseResult<?> runtimeExceptionHandler(RuntimeException e) {
        if (e.getCause() instanceof BusinessException businessEx) {
            log.error("BusinessException(wrapped by Seata): {}", businessEx.getMessage(), businessEx);
            return ResponseResult.fail(businessEx.getCode(), businessEx.getMessage());
        }
        log.error("RuntimeException: {}", e.getMessage(), e);
        return ResponseResult.fail(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation failed for request: {} {}", request.getMethod(), request.getRequestURI());
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.error("MethodArgumentNotValidException: {}", message);
        return ResponseResult.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<?> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("Unexpected exception occurred for request: {} {}", request.getMethod(), request.getRequestURI(), e);
        return ResponseResult.fail(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }


}
