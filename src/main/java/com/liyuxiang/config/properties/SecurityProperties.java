package com.liyuxiang.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liyuxiang
 * @Date: 2025/8/13 13:49
 * Security 配置属性
 */
@ConfigurationProperties(prefix = "security.ignore")
@Data
public class SecurityProperties {

    /**
     * 排除路径
     */
    private List<String> whites = new ArrayList<>();

}
