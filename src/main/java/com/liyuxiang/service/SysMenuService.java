package com.liyuxiang.service;

import java.util.Collection;

public interface SysMenuService {
    Collection<String> selectMenuPermsByUserId(Long userId);
}
