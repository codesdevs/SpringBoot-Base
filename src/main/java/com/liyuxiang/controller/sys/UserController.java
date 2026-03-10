package com.liyuxiang.controller.sys;

import com.liyuxiang.common.result.ResponseResult;
import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;
import com.liyuxiang.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 14:30
 * @Description: TODO
 */
@RestController
@RequestMapping("/sys/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseResult<String> login(@RequestBody @Validated LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return ResponseResult.success(token);
    }

    @PostMapping("register")
    public ResponseResult<String> register(@RequestBody @Validated RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseResult.success();
    }
}
