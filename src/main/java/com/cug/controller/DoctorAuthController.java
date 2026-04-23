package com.cug.controller;

import com.cug.domain.dto.DoctorLoginDTO;
import com.cug.domain.pojo.R;
import com.cug.service.DoctorAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor/api")
public class DoctorAuthController {
    private final DoctorAuthService doctorAuthService;
    public DoctorAuthController(DoctorAuthService doctorAuthService) {
        this.doctorAuthService = doctorAuthService;
    }
    @PostMapping("/login")
    public R login(@RequestBody DoctorLoginDTO doctorLoginDTO) {
        return doctorAuthService.login(doctorLoginDTO);
    }
}
