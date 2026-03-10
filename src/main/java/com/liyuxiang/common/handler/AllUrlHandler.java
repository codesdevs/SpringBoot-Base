package com.liyuxiang.common.handler;

import cn.hutool.core.util.ReUtil;
import com.liyuxiang.common.utils.SpringUtils;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: liyuxiang
 * @Date: 2025/8/13 13:49
 * 获取所有Url配置
 */
@Data
@Lazy
@Component
public class AllUrlHandler implements InitializingBean {

    private List<String> urls = new ArrayList<>(256);

    @Override
    public void afterPropertiesSet() {
        String name = "requestMappingHandlerMapping";
        RequestMappingHandlerMapping mapping = SpringUtils.getBean(name, RequestMappingHandlerMapping.class);

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

        Pattern pattern = Pattern.compile("\\{(.*?)\\}");

        Set<String> handlerSet = handlerMethods.keySet().stream()
                                               .flatMap(this::extractPatterns)  // 使用新方法提取所有可能的路径
                                               .collect(Collectors.toSet());

        // 获取注解上边的 path 替代 path variable 为 *
        handlerSet.stream()
                  .map(path -> ReUtil.replaceAll(path, pattern, "*"))
                  .forEach(urls::add);
    }
    /**
     * 从 RequestMappingInfo 提取所有可能的路径模式
     */
    private Stream<String> extractPatterns(RequestMappingInfo info) {
        // 处理 patternsCondition (Spring MVC 5.3+ 之前的方式)
        if (info.getPatternsCondition() != null) {
            return info.getPatternsCondition().getPatterns().stream();
        }

        // 处理 pathPatternsCondition (Spring MVC 5.3+ 的新方式)
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition().getPatterns().stream()
                       .map(PathPattern::getPatternString);
        }

        // 如果都没有，返回空流
        return Stream.empty();
    }
}
