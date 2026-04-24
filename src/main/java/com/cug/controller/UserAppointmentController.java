package com.cug.controller;

import com.cug.domain.pojo.R;
import com.cug.service.UserAppointmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/api/appointment")
public class UserAppointmentController {
    private final UserAppointmentService userAppointmentService;
    public UserAppointmentController(UserAppointmentService userAppointmentService) {
        this.userAppointmentService = userAppointmentService;
    }
    @GetMapping("/doctors")
    public R getDoctorList()
    {
        return userAppointmentService.getDoctorList();
    }

}
