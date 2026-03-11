package com.liyuxiang.model.security;

import cn.dev33.satoken.stp.StpInterface;
import com.liyuxiang.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author: liyuxiang
 * @Date: 2026/03/11 09:33
 * sa-token 权限管理实现类
 */
@Slf4j
public class SaPermissionImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        log.info("getPermissionList({}, {})", loginType, loginId);

        // 检查用户是否已登录（通过SecurityContextHolder检查是否有用户信息）
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();
        System.out.println("用户权限列表：");
//        System.out.println(SecurityUtils.getLoginUser().getPermissions());
        if (userId == null || userId == 0L) {
            log.warn("用户未登录，无法获取权限列表");
            return new ArrayList<>();
        }

        log.info("为用户 {} ({}) 获取权限列表", username, userId);

        List<String> permissions = new ArrayList<>();

        log.info("用户 {} 的权限列表: {}", username, permissions);
//        return SecurityUtils.getLoginUser().getPermissions().stream().toList();
        return  null;
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return List.of();
    }
}
