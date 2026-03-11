package com.liyuxiang.service.impl;

import com.liyuxiang.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {
    @Override
    public Collection<String> selectMenuPermsByUserId(Long userId) {
        return List.of();
    }
}
