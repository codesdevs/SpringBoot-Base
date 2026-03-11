package com.liyuxiang.service.impl;

import com.liyuxiang.common.utils.SecurityUtils;
import com.liyuxiang.service.SysMenuService;
import com.liyuxiang.service.SysPermissionService;
import com.liyuxiang.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysRoleService roleService;
    private final SysMenuService menuService;

    /**
     * 获取角色数据权限
     *
     * @param userId 用户id
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(Long userId) {
        Set<String> roles = new HashSet<>();
        // 管理员拥有所有权限
//        if (SecurityUtils.isSuperAdmin(userId)) {
//            roles.add(TenantConstants.SUPER_ADMIN_ROLE_KEY);
//        } else {
//            roles.addAll(roleService.selectRolePermissionByUserId(userId));
//        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户id
     * @return 菜单权限信息
     */
    @Override
    public Set<String> getMenuPermission(Long userId) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (SecurityUtils.isSuperAdmin(userId)) {
            perms.add("*:*:*");
        } else {
            perms.addAll(menuService.selectMenuPermsByUserId(userId));
        }
        return perms;
    }
}
