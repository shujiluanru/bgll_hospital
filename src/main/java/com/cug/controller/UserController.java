package com.cug.controller;

import com.cug.domain.dto.UserInfoDTO;
import com.cug.domain.pojo.R;
import com.cug.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/api/common")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService)
    {
        this.userService=userService;
    }
    @PutMapping("/userInfo")
    public R updateUserInfo(@RequestBody UserInfoDTO userInfoDTO)
    {
        return userService.updateUserInfo(userInfoDTO);
    }
    @GetMapping("/reservations")
    public R getReservations()
    {
        return userService.getReservations();
    }
}
