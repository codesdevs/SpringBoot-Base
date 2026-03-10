package com.liyuxiang.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liyuxiang.common.constant.CacheConstants;
import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.enums.UserStatus;
import com.liyuxiang.common.exception.BusinessException;
import com.liyuxiang.common.utils.RedisUtils;
import com.liyuxiang.common.utils.ServletUtils;
import com.liyuxiang.common.utils.StringUtils;
import com.liyuxiang.mapper.UserMapper;
import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;
import com.liyuxiang.model.pojo.sys.SysUser;
import com.liyuxiang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 14:42
 * @Description: TODO
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public String login(LoginDTO loginDTO) {
        validateCaptcha(loginDTO.getCode(),loginDTO.getUuid());


        try {
            SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<>(SysUser.class).eq(SysUser::getUserName, loginDTO.getUserName()));
            if (sysUser == null || !passwordEncoder.matches(loginDTO.getPassword(), sysUser.getPassword())) {
                throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
            }
            // 检查账号状态
            Integer status = sysUser.getStatus();
            if (!UserStatus.NORMAL.getValue().equals(status)) {
                if (UserStatus.DISABLED.getValue().equals(status)) {
                    throw new BusinessException(ResultCodeEnum.USER_DISABLED);
                }
                if (UserStatus.LOCKED.getValue().equals(status)) {
                    throw new BusinessException(ResultCodeEnum.USER_LOCKED);
                }
                // 其他非正常状态统一报用户名或密码错误（防止泄露状态信息）
                throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
            }

            // 登录成功，生成 token
            StpUtil.login(sysUser.getUserId());

            // 可选：记录登录 IP、登录时间等（如果 SysUser 有 loginIp、loginDate 字段）
            sysUser.setLoginIp(ServletUtils.getRequest().getRemoteAddr());
            sysUser.setLoginDate(LocalDateTime.now());
             userMapper.updateById(sysUser);

            log.info("用户登录成功：userId={}, username={}", sysUser.getUserId(), sysUser.getUserName());

            // 返回 token（前端存到 localStorage 或 cookie）
            return StpUtil.getTokenValue();


        }catch (Exception e){
            log.error("用户验证失败: {}", e.getMessage());
            throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
        }
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