package com.liyuxiang.model.pojo.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liyuxiang.model.pojo.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

/**
 * @Author: liyuxiang
 * @Date: 2025/10/1 14:14
 * @Description: 部门表 sys_dept
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_dept")
@TableName(value = "sys_dept")
public class SysDept extends BaseEntity {
    @Id
    @TableId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("部门ID")
    private Long deptId;

    /** 部门名称 */
    @Comment("部门名称")
    private String deptName;

    /** 父部门ID */
    @Comment("父部门ID")
    private Long parentId;

    /** 祖级列表 */
    @Comment("祖级列表")
    private String ancestors;

    /** 显示顺序 */
    @Comment("显示顺序")
    private Integer orderNum;

    /** 负责人 */
    @Comment("负责人")
    private String leader;

    /** 联系电话 */
    @Comment("联系电话")
    private String phone;

    /** 邮箱 */
    @Comment("邮箱")
    private String email;

    /** 部门状态:0正常,1停用 */
    @Comment("部门状态:0正常,1停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    @Comment("删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /** 父部门名称 */
    @Comment("父部门名称")
    @Transient
    private String parentName;

}
