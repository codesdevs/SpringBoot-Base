package com.liyuxiang.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liyuxiang.common.constant.CacheConstants;
import com.liyuxiang.common.constant.Constants;
import com.liyuxiang.common.constant.SecurityConstants;
import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.enums.UserStatus;
import com.liyuxiang.common.exception.BusinessException;
import com.liyuxiang.common.utils.*;
import com.liyuxiang.mapper.SysUserMapper;
import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;
import com.liyuxiang.model.dto.RoleDTO;
import com.liyuxiang.model.pojo.sys.SysUser;
import com.liyuxiang.model.security.LoginUser;
import com.liyuxiang.model.vo.SysUserVo;
import com.liyuxiang.model.vo.UserInfoVo;
import com.liyuxiang.service.SysPermissionService;
import com.liyuxiang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 14:42
 * @Description: TODO
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final SysUserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final SysPermissionService permissionService;
    public UserServiceImpl(SysUserMapper userMapper, SysPermissionService permissionService) {
        this.permissionService = permissionService;
        this.userMapper = userMapper;
    }
    @Override
    public String login(LoginDTO loginDTO) {
        validateCaptcha(loginDTO.getCode(),loginDTO.getUuid());
        SysUserVo user = loadUserByUsername(loginDTO.getUserName());
        checkLogin(loginDTO.getUserName(), () -> !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        StpUtil.login(loginUser.getUserId());
        // 将 LoginUser 存到 Sa-Token Session 中，方便后续获取用户
        StpUtil.getTokenSession().set(SecurityConstants.LOGIN_USER, loginUser);

        recordLoginInfo(user.getUserId());
        return StpUtil.getTokenValue();
    }

    private SysUserVo loadUserByUsername(String username) {

        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().select(SysUser::getUserName, SysUser::getStatus).eq(SysUser::getUserName, username));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new BusinessException(ResultCodeEnum.USER_NOT_EXIST);
        } else if (UserStatus.DISABLED.getValue().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BusinessException(ResultCodeEnum.USER_DISABLED);
        }

        return userMapper.selectUserByUserName(username);
    }
    /**
     * 登录校验
     */
    private void checkLogin(String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;

        // 获取用户登录错误次数(可自定义限制策略 例如: key + username + ip)
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);
        // 锁定时间内登录 则踢出
        if (ObjectUtil.isNotNull(errorNumber) && errorNumber.equals(CacheConstants.PASSWORD_MAX_RETRY_COUNT)) {
            throw new BusinessException(ResultCodeEnum.USER_LOCKED);
        }

        if (supplier.get()) {
            // 是否第一次
            errorNumber = ObjectUtil.isNull(errorNumber) ? 1 : errorNumber + 1;
            // 达到规定错误次数 则锁定登录
            if (errorNumber.equals(CacheConstants.PASSWORD_MAX_RETRY_COUNT)) {
                RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(CacheConstants.PASSWORD_LOCK_TIME));
                throw new BusinessException(ResultCodeEnum.USER_LOCKED);
            } else {
                // 未达到规定错误次数 则递增
                RedisUtils.setCacheObject(errorKey, errorNumber);
                throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
            }
        }

        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }
    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(Objects.requireNonNull(ServletUtils.getRequest()).getRemoteAddr());
        sysUser.setLoginDate(LocalDateTime.now());
        sysUser.setUpdateBy(userId);
        userMapper.updateById(sysUser);
    }
    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
//        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUserName(user.getUserName());
        loginUser.setAvatar(user.getAvatar());
//        loginUser.setUserType(user.getUserType());
//        loginUser.setKroleGroupIds(user.getKroleGroupIds());
//        loginUser.setKroleGroupType(user.getKroleGroupType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(user.getUserId()));
        loginUser.setRolePermission(permissionService.getRolePermission(user.getUserId()));
        loginUser.setDeptName(ObjectUtil.isNull(user.getDept()) ? "" : user.getDept().getDeptName());
        List<RoleDTO> roles = BeanUtil.copyToList(user.getRoles(), RoleDTO.class);
        loginUser.setRoles(roles);
        return loginUser;
    }


    @Override
    public void register(RegisterDTO registerDTO) {
        validateCaptcha(registerDTO.getCode(),registerDTO.getUuid());
        SysUser exist = userMapper.selectOne(new LambdaQueryWrapper<>(SysUser.class).eq(SysUser::getUserName, registerDTO.getUserName()));
        if (exist != null) {
            throw new BusinessException(ResultCodeEnum.USERNAME_ALREADY_EXISTS);
        }
        SysUser registerUser = new SysUser();
        registerUser.setUserName(StringUtils.trim(registerDTO.getUserName()));
        registerUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        registerUser.setStatus(UserStatus.NORMAL.getValue());
        userMapper.insert(registerUser);

    }

    @Override
    public UserInfoVo getUserInfo() {
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = SecurityUtils.getLoginUser();

        SysUserVo user = userMapper.selectUserById(loginUser.getUserId());
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getMenuPermission());
        userInfoVo.setRoles(loginUser.getRolePermission());

        return userInfoVo;
    }

    /**
     * 校验验证码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        if (captcha == null)
        {
            throw new BusinessException(ResultCodeEnum.AUTH_CAPTCHA_INVALID);
        }
        RedisUtils.deleteObject(verifyKey);
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new BusinessException(ResultCodeEnum.AUTH_CAPTCHA_INVALID);
        }
    }
}