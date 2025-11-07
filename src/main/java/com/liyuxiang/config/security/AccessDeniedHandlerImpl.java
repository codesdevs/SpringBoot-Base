package com.liyuxiang.config.security;

import com.alibaba.fastjson2.JSON;
import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.result.ResponseResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author: liyuxiang
 * @Date: 2025/11/07
 * 权限拒绝处理器 - 当用户已认证但权限不足时触发
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("权限不足，请求路径：{}，异常信息：{}", request.getRequestURI(), accessDeniedException.getMessage());

        // 构建统一的 JSON 响应
        ResponseResult<Void> result = ResponseResult.fail(ResultCodeEnum.FORBIDDEN);

        // 设置响应头
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 写入响应
        response.getWriter().write(JSON.toJSONString(result));
    }
}