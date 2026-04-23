package com.cug.service.serviceImpl;

import com.cug.domain.dto.UserInfoDTO;
import com.cug.domain.pojo.R;
import com.cug.mapper.UserMapper;
import com.cug.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public R updateUserInfo(UserInfoDTO userInfoDTO) {
        userMapper.updateUserById(userInfoDTO);
        log.info("updateUserInfo cug");
        return R.ok();
    }
}
