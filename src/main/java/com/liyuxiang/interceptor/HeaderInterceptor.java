package com.liyuxiang.interceptor;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.liyuxiang.common.constant.SecurityConstants;
import com.liyuxiang.common.context.SecurityContextHolder;
import com.liyuxiang.model.security.LoginUser;
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

        // 直接从 TokenSession 获取用户信息（不重复校验登录）
        if (StpUtil.isLogin()) {
            SaSession tokenSession = StpUtil.getTokenSession();
            if (tokenSession != null) {
                LoginUser loginUser = tokenSession.getModel(SecurityConstants.LOGIN_USER, LoginUser.class);
                if (loginUser != null) {
                    SecurityContextHolder.setUserId(String.valueOf(loginUser.getUserId()));
                    SecurityContextHolder.setUserName(loginUser.getUser().getUserName());
                    SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
