package com.liyuxiang.config;

import com.liyuxiang.config.properties.SecurityProperties;
import com.liyuxiang.config.security.AccessDeniedHandlerImpl;
import com.liyuxiang.config.security.AuthenticationEntryPointImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

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
public class SecurityConfig {
    private final SecurityProperties securityProperties;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @PostConstruct
    public void init() {
        log.info("加载Security权限配置，忽略路径：{}", Arrays.toString(securityProperties.getWhites().toArray()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护
                .csrf(csrf -> csrf.disable())
                // 配置 Session 管理策略为无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用表单登录
                .formLogin(form -> form.disable())
                // 禁用 HTTP Basic 认证
                .httpBasic(basic -> basic.disable())
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        // 认证失败处理器（401 未认证）
                        .authenticationEntryPoint(authenticationEntryPoint)
                        // 权限拒绝处理器（403 权限不足）
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // 配置路径权限规则
                .authorizeHttpRequests(auth -> auth
                        // 白名单路径允许匿名访问
                        .requestMatchers(securityProperties.getWhites().toArray(new String[0])).permitAll()
                        // 其他所有请求允许访问（由拦截器进行认证控制）
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
