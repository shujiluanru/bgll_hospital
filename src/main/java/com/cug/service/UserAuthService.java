package com.cug.service;

import com.cug.domain.dto.UserLoginDTO;
import com.cug.domain.pojo.R;

public interface UserAuthService {
    R login(UserLoginDTO userLoginDTO);
}
