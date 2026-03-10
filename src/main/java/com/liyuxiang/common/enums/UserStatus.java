package com.liyuxiang.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: liyuxiang
 * @Date: 2026/03/10 14:33
 * @Description: 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    NORMAL(0, "正常", "success"),      // value, desc, 前端 tag 类型（可选）
    DISABLED(1, "禁用/封禁", "danger"),
    LOCKED(2, "锁定（密码错误过多）", "warning"),
    PENDING(3, "待审核", "processing"),
    LOGOFF(9, "已注销", "default");

    @EnumValue
    private final Integer value;

    private final String desc;

    private final String tagType;

    public static UserStatus fromValue(Integer value) {
        if (value == null) return null;
        for (UserStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的用户状态: " + value);
    }

    public static String getDescByValue(Integer value) {
        UserStatus status = fromValue(value);
        return status != null ? status.desc : "未知";
    }
}