package com.liyuxiang.controller.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.liyuxiang.common.result.ResponseResult;
import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;
import com.liyuxiang.model.security.LoginUser;
import com.liyuxiang.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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

    @SaCheckLogin
    @GetMapping("/info")
    public ResponseResult<Object> getUserInfo() {
      HashMap<String,Object> resultMap =  userService.getUserInfo();
      return ResponseResult.success(resultMap);
    }
}
