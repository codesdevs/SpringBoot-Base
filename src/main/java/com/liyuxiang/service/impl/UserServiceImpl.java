package com.liyuxiang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liyuxiang.common.enums.ResultCodeEnum;
import com.liyuxiang.common.exception.BusinessException;
import com.liyuxiang.common.utils.SecurityUtils;
import com.liyuxiang.mapper.UserMapper;
import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.pojo.sys.SysUser;
import com.liyuxiang.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 14:42
 * @Description: TODO
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public String login(LoginDTO loginDTO) {
        try {
            SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<>(SysUser.class).eq(SysUser::getUserName, loginDTO.getUserName()));
            if (sysUser == null || !SecurityUtils.matchesPassword(loginDTO.getPassword(), sysUser.getPassword())) {
                throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
            }
            // TODO 生成token

        }catch (Exception e){
            log.error("用户验证失败: {}", e.getMessage());
            throw new BusinessException(ResultCodeEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        return "";
    }
}