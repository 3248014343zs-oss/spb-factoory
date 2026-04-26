package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Select("SELECT id, username, password, nickname, email, phone, role, status, create_time, update_time " +
            "FROM sys_user WHERE username = #{username} AND status = 1")
    User getByUsername(String username);


    @Select("SELECT * FROM sys_user WHERE uid = #{uid}")
    User selectByUid(Long uid);
}