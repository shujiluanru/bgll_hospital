package com.cug.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.cug.domain.pojo.User;
@Mapper
public interface UserAuthMapper {
    User getUserByPhone(String phone);

    void insertNewUser(User user);
}
