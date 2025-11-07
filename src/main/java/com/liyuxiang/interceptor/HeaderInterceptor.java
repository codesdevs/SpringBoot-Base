package com.liyuxiang.interceptor;

import com.liyuxiang.common.constant.SecurityConstants;
import com.liyuxiang.common.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author liyuxiang
 */
@Slf4j
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 检查是否为内部请求，如果是则跳过认证
        String innerFlag = request.getHeader(SecurityConstants.INNER);
        if ("true".equals(innerFlag)) {
            log.debug("检测到内部请求，跳过认证拦截");
            return true;
        }

        // 获取用户信息（从请求头中提取）
        String userId = request.getHeader(SecurityConstants.DETAILS_USER_ID);
        String username = request.getHeader(SecurityConstants.DETAILS_USERNAME);
        String userKey = request.getHeader(SecurityConstants.USER_KEY);

        // 如果请求头中存在用户信息，则存储到上下文中
        if (userId != null && !userId.isEmpty()) {
            SecurityContextHolder.setUserId(userId);
            log.debug("设置用户ID到上下文：{}", userId);
        }

        if (username != null && !username.isEmpty()) {
            SecurityContextHolder.setUserName(username);
            log.debug("设置用户名到上下文：{}", username);
        }

        if (userKey != null && !userKey.isEmpty()) {
            SecurityContextHolder.setUserKey(userKey);
            log.debug("设置用户Key到上下文：{}", userKey);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
