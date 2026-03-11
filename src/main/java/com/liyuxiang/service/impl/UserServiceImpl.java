package com.liyuxiang.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liyuxiang.common.constant.CacheConstants;
import com.liyuxiang.common.constant.Constants;
import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.enums.UserStatus;
import com.liyuxiang.common.exception.BusinessException;
import com.liyuxiang.common.utils.*;
import com.liyuxiang.mapper.SysUserMapper;
import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;
import com.liyuxiang.model.pojo.sys.SysUser;
import com.liyuxiang.model.security.LoginUser;
import com.liyuxiang.model.vo.SysUserVo;
import com.liyuxiang.model.vo.UserInfoVo;
import com.liyuxiang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    public UserServiceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public String login(LoginDTO loginDTO) {
        validateCaptcha(loginDTO.getCode(),loginDTO.getUuid());


//        try {
//            SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<>(SysUser.class).eq(SysUser::getUserName, loginDTO.getUserName()));
//            if (sysUser == null || !passwordEncoder.matches(loginDTO.getPassword(), sysUser.getPassword())) {
//                throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
//            }
//            // 检查账号状态
//            Integer status = sysUser.getStatus();
//            if (!UserStatus.NORMAL.getValue().equals(status)) {
//                if (UserStatus.DISABLED.getValue().equals(status)) {
//                    throw new BusinessException(ResultCodeEnum.USER_DISABLED);
//                }
//                if (UserStatus.LOCKED.getValue().equals(status)) {
//                    throw new BusinessException(ResultCodeEnum.USER_LOCKED);
//                }
//                // 其他非正常状态统一报用户名或密码错误（防止泄露状态信息）
//                throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
//            }
//
//            // 登录成功，生成 token
//            StpUtil.login(sysUser.getUserId());
//            LoginUser loginUser = buildLoginUser(sysUser);
//            // 将 LoginUser 存到 Sa-Token Session 中，方便后续获取用户信息
//            StpUtil.getTokenSession().set(SecurityConstants.LOGIN_USER, loginUser);
//
//            // 可选：记录登录 IP、登录时间等（如果 SysUser 有 loginIp、loginDate 字段）
//            sysUser.setLoginIp(ServletUtils.getRequest().getRemoteAddr());
//            sysUser.setLoginDate(LocalDateTime.now());
//            userMapper.updateById(sysUser);
//
//            log.info("用户登录成功：userId={}, username={}", sysUser.getUserId(), sysUser.getUserName());
//
//            // 返回 token（前端存到 localStorage 或 cookie）
//            return StpUtil.getTokenValue();
//
//
//        }catch (Exception e){
//            log.error("用户验证失败: {}", e.getMessage());
//            throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
//        }
        SysUserVo user = loadUserByUsername(loginDTO.getUserName());
        checkLogin(loginDTO.getUserName(), () -> !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = buildLoginUser(user);
        // 生成token
        LoginHelper.loginByDevice(loginUser);
        StpUtil.login(loginUser.getUserId());

//        recordLogininfor(loginUser.getTenantId(), username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        recordLoginInfo(user.getUserId());
        return StpUtil.getTokenValue();
    }

//    private LoginUser buildLoginUser(SysUser sysUser) {
//        LoginUser loginUser = new LoginUser();
////        loginUser.setUser(sysUser);
//        loginUser.setUserId(sysUser.getUserId());
//        return loginUser;
//    }
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
        String loginFail = Constants.LOGIN_FAIL;

        // 获取用户登录错误次数(可自定义限制策略 例如: key + username + ip)
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);
        // 锁定时间内登录 则踢出
        if (ObjectUtil.isNotNull(errorNumber) && errorNumber.equals(CacheConstants.PASSWORD_MAX_RETRY_COUNT)) {
//            recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
//            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            throw new BusinessException(ResultCodeEnum.USER_LOCKED);
        }

        if (supplier.get()) {
            // 是否第一次
            errorNumber = ObjectUtil.isNull(errorNumber) ? 1 : errorNumber + 1;
            // 达到规定错误次数 则锁定登录
            if (errorNumber.equals(CacheConstants.PASSWORD_MAX_RETRY_COUNT)) {
                RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(CacheConstants.PASSWORD_LOCK_TIME));
//                recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
//                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
                throw new BusinessException(ResultCodeEnum.USER_LOCKED);
            } else {
                // 未达到规定错误次数 则递增
                RedisUtils.setCacheObject(errorKey, errorNumber);
//                recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
//                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
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
//        loginUser.setDeptId(user.getDeptId());
        loginUser.setUserName(user.getUserName());
        loginUser.setAvatar(user.getAvatar());
//        loginUser.setUserType(user.getUserType());
//        loginUser.setKroleGroupIds(user.getKroleGroupIds());
//        loginUser.setKroleGroupType(user.getKroleGroupType());
//        loginUser.setMenuPermission(permissionService.getMenuPermission(user.getUserId()));
//        loginUser.setRolePermission(permissionService.getRolePermission(user.getUserId()));
//        loginUser.setDeptName(ObjectUtil.isNull(user.getDept()) ? "" : user.getDept().getDeptName());
//        List<RoleDTO> roles = BeanUtil.copyToList(user.getRoles(), RoleDTO.class);
//        loginUser.setRoles(roles);
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

//        // 直接从 Sa-Token Session 获取 LoginUser（已登录拦截器保证非 null）
//        LoginUser loginUser = SecurityUtils.getLoginUser();
//        if (loginUser == null) {
//            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
//        }
//
//        // 如果 LoginUser 里已经存了 SysUser，直接用；否则从 DB 查一次
//        SysUser sysUser = loginUser.getUser();
//        if (sysUser == null) {
//            sysUser = userMapper.selectById(loginUser.getUserId());
//            // 可选：回填到 loginUser 中，避免下次再查
//            loginUser.setUser(sysUser);
//            StpUtil.getTokenSession().set(SecurityConstants.LOGIN_USER, loginUser);
//        }
//
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("user", sysUser);
//        result.put("permissions", loginUser.getRolePermission()); // 如果有权限
//        // 可加更多：roles, avatar 等

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