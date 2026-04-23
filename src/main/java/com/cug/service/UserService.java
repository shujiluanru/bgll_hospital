package com.cug.service;

import com.cug.domain.dto.UserInfoDTO;
import com.cug.domain.pojo.R;

public interface UserService {
    R updateUserInfo(UserInfoDTO userInfoDTO);
}
