package com.gymcn.models.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gymcn.models.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
