package com.cug.mapper;

import com.cug.domain.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void updateUserById(UserInfoDTO userInfoDTO);

    List<Long> getIds();
}
