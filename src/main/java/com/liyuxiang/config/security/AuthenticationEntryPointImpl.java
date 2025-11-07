package com.liyuxiang.config.security;

import com.alibaba.fastjson2.JSON;
import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.result.ResponseResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author: liyuxiang
 * @Date: 2025/11/07
 * 认证失败处理器 - 当用户未认证访问需要认证的资源时触发
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("认证失败，请求路径：{}，异常信息：{}", request.getRequestURI(), authException.getMessage());

        // 构建统一的 JSON 响应
        ResponseResult<Void> result = ResponseResult.fail(ResultCodeEnum.UNAUTHORIZED);

        // 设置响应头
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 写入响应
        response.getWriter().write(JSON.toJSONString(result));
    }
}