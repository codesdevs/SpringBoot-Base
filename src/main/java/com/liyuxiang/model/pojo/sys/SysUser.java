package com.liyuxiang.model.pojo.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liyuxiang.model.pojo.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 13:29
 * @Description: sys_user 实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_user")
@TableName(value = "sys_user")
public class SysUser extends BaseEntity {
    @Id
    @TableId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    private String password;

    private String nickName;

    private String email;

    private String phone;

    private String sex;

    private String avatar;

    private Integer status;

    private String loginIp;

    private LocalDateTime loginDate;
}
