package com.liyuxiang.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 14:36
 * @Description: 用户登录对象
 */
@Data
public class LoginDTO {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
    private String userName;

    /**
     * 用户密码
     */
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "用户密码长度必须在6到20个字符之间")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;

}
