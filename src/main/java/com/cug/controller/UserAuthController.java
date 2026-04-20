package com.cug.controller;

import com.cug.annotation.BgllLog;
import com.cug.domain.dto.UserLoginDTO;
import com.cug.domain.dto.UserRegisterDTO;
import com.cug.domain.pojo.R;
import com.cug.service.UserAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/api")
public class UserAuthController {
    private final UserAuthService userAuthService;
    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }
    @PostMapping("/login/password")
    public R login1(@RequestBody UserLoginDTO userLoginDTO) {
        return userAuthService.login(userLoginDTO);
    }
    @PostMapping("/login/code")
    public R login2(@RequestBody UserLoginDTO userLoginDTO) {
        return userAuthService.login(userLoginDTO);
    }
    @PostMapping("/send-code")
    public R sendCode(@RequestBody Map<String,String> data)
    {
        String phone=data.get("phone");
        return userAuthService.sendSmsCode(phone);
    }
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterDTO userRegisterDTO)
    {
        return userAuthService.register(userRegisterDTO);
    }

}
