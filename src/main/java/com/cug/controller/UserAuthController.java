package com.cug.controller;

import com.cug.annotation.BgllLog;
import com.cug.domain.dto.UserLoginDTO;
import com.cug.domain.pojo.R;
import com.cug.service.UserAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/api")
public class UserAuthController {
    private final UserAuthService userAuthService;
    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }
    @PostMapping("/login/*")
    public R login(UserLoginDTO userLoginDTO) {

        return userAuthService.login();
    }
}
