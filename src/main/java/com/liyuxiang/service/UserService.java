package com.liyuxiang.service;

import com.liyuxiang.model.dto.LoginDTO;
import com.liyuxiang.model.dto.RegisterDTO;

public interface UserService {

    String login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);
}
