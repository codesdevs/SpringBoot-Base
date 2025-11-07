package com.liyuxiang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyuxiang.model.pojo.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
