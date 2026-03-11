package com.liyuxiang.service;

import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;

import java.util.HashMap;

public interface UserService {

    String login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    HashMap<String, Object> getUserInfo();
}
