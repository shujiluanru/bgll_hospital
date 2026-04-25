package com.cug.controller;

import com.cug.domain.pojo.R;
import com.cug.service.UserAppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("/resources")
    public R getResources()
    {
        return userAppointmentService.getResources();
    }
    @PostMapping("/grab")
    public R grab(@RequestBody Map<String,String> data)
    {
        Long doctorId = Long.parseLong(data.get("doctorId"));
        return userAppointmentService.grab(doctorId);
    }



}
