package com.liyuxiang.model.pojo.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sys_user_role")
@TableName(value = "sys_user_role")
public class SysUserRole {

    /**
     * 用户ID
     */
    @Id
    @TableId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

}