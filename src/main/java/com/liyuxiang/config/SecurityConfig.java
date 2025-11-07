package com.liyuxiang.config;

import com.liyuxiang.config.properties.SecurityProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
public class SecurityConfig {
    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        log.info("加载Security权限配置，忽略路径：{}", Arrays.toString(securityProperties.getWhites().toArray()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 配置安全规则，使用忽略的路径
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.getWhites().toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
