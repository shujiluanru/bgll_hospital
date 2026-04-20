package com.cug.service;

import com.cug.domain.dto.UserLoginDTO;
import com.cug.domain.dto.UserRegisterDTO;
import com.cug.domain.pojo.R;

public interface UserAuthService {
    R login(UserLoginDTO userLoginDTO);
    R sendSmsCode(String phone);

    R register(UserRegisterDTO userRegisterDTO);
}
