package com.liyuxiang.service.impl;

import com.liyuxiang.common.page.PageQuery;
import com.liyuxiang.common.page.TableDataInfo;
import com.liyuxiang.model.bo.SysRoleBo;
import com.liyuxiang.model.pojo.sys.SysUserRole;
import com.liyuxiang.model.vo.SysRoleVo;
import com.liyuxiang.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SysRoleServiceImpl implements SysRoleService {
    @Override
    public TableDataInfo<SysRoleVo> selectPageRoleList(SysRoleBo role, PageQuery pageQuery) {
        return null;
    }

    @Override
    public List<SysRoleVo> selectRoleList(SysRoleBo role) {
        return List.of();
    }

    @Override
    public List<SysRoleVo> selectRolesByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        return Set.of();
    }

    @Override
    public List<SysRoleVo> selectRoleAll() {
        return List.of();
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return List.of();
    }

    @Override
    public SysRoleVo selectRoleById(Long roleId) {
        return null;
    }

    @Override
    public boolean checkRoleNameUnique(SysRoleBo role) {
        return false;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRoleBo role) {
        return false;
    }

    @Override
    public void checkRoleAllowed(Long roleId) {

    }

    @Override
    public void checkRoleDataScope(Long roleId) {

    }

    @Override
    public long countUserRoleByRoleId(Long roleId) {
        return 0;
    }

    @Override
    public int insertRole(SysRoleBo bo) {
        return 0;
    }

    @Override
    public int updateRole(SysRoleBo bo) {
        return 0;
    }

    @Override
    public int updateRoleStatus(Long roleId, String status) {
        return 0;
    }

    @Override
    public int authDataScope(SysRoleBo bo) {
        return 0;
    }

    @Override
    public int deleteRoleById(Long roleId) {
        return 0;
    }

    @Override
    public int deleteRoleByIds(Long[] roleIds) {
        return 0;
    }

    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return 0;
    }

    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return 0;
    }

    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        return 0;
    }

    @Override
    public void cleanOnlineUserByRole(Long roleId) {

    }
}
