package com.cug.service.serviceImpl;

import com.cug.domain.dto.UserLoginDTO;
import com.cug.domain.pojo.R;
import com.cug.exception.ParamException;
import com.cug.mapper.UserAuthMapper;
import com.cug.service.UserAuthService;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    private final UserAuthMapper userAuthMapper;
    public UserAuthServiceImpl(UserAuthMapper userAuthMapper) {
        this.userAuthMapper = userAuthMapper;
    }
    @Override
    public R login(UserLoginDTO userLoginDTO) {
        //判断登录方式
        if (userLoginDTO.getPhone() == null) {
            throw new ParamException("手机号不能为空");
        }
        if(userLoginDTO.getPassword().isBlank())
        {
            //验证码登录
        }
        else
        {
            //密码登录

        }
        return R.ok();
    }
}
