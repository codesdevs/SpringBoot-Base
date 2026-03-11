package com.liyuxiang.model.vo;

import lombok.Data;

import java.util.Set;

@Data
public class UserInfoVo {
    /**
     * 用户基本信息
     */
    private SysUserVo user;

    /**
     * 菜单权限
     */
    private Set<String> permissions;

    /**
     * 角色权限
     */
    private Set<String> roles;
}
