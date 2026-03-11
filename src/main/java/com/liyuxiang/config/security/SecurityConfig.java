package com.liyuxiang.config.security;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.liyuxiang.common.constant.SecurityConstants;
import com.liyuxiang.common.handler.AllUrlHandler;
import com.liyuxiang.common.utils.SpringUtils;
import com.liyuxiang.config.properties.SecurityProperties;
import com.liyuxiang.dao.PlusSaTokenDao;
import com.liyuxiang.interceptor.HeaderInterceptor;
import com.liyuxiang.model.security.SaPermissionImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * @Author: liyuxiang
 * @Date: 2025/8/13 13:49
 * Security 权限安全配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        log.info("白名单路径加载: {}", Arrays.toString(securityProperties.getWhites().toArray()));
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 先注册HeaderInterceptor，用于设置用户上下文信息
        registry.addInterceptor(new HeaderInterceptor()).excludePathPatterns("/error");

        // 2. 注册Sa-Token拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
                    AllUrlHandler allUrlHandler = SpringUtils.getBean(AllUrlHandler.class);
                    // 检查是否为内部请求，如果是则跳过认证
                    String innerFlag = SaHolder.getRequest().getHeader(SecurityConstants.INNER);
                    if ("true".equals(innerFlag)) {
                        log.debug("检测到内部请求，跳过Sa-Token认证拦截");
                        return;
                    }

                    // 登录验证 -- 排除多个路径
                    SaRouter
                            // 获取所有的
                            .match(allUrlHandler.getUrls())
                            // 对未排除的路径进行检查
                            .check(() -> {
                                // 检查是否登录 是否有token
                                StpUtil.checkLogin();

                                // 有效率影响 用于临时测试
                                // if (log.isDebugEnabled()) {
                                //     log.debug("剩余有效时间: {}", StpUtil.getTokenTimeout());
                                //     log.debug("临时有效时间: {}", StpUtil.getTokenActivityTimeout());
                                // }

                            });
                })).addPathPatterns("/**")
                // 排除不需要拦截的路径
                .excludePathPatterns(securityProperties.getWhites()).excludePathPatterns("/error").order(0);
        ;
    }
    //密码
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Sa-Token权限管理实现类
    @Bean
    public StpInterface stpInterface() {
        return new SaPermissionImpl();
    }

    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        return new PlusSaTokenDao();
    }
}
