package com.cug.controller;

import com.cug.domain.pojo.R;
import com.cug.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/doctor/api/common")
public class DoctorController {
    private final DoctorService doctorService;
    public DoctorController(DoctorService doctorService)
    {
        this.doctorService=doctorService;
    }
    @PutMapping("/description")
    public R updateDescription(@RequestBody Map<String,String> data)
    {
        return doctorService.updateDescription(data);
    }
    @GetMapping("/release_log")
    public R getReleaseLog()
    {
        return doctorService.getReleaseLog();
    }
    /**
     * 医生发布号源，添加号源，高并发难点
     *data:
     * param:releaseNum
     */
    @PostMapping("/release")
    public R release(@RequestBody Map<String,String> data)
    {
        return doctorService.release(data);
    }
    @GetMapping("/reservations")
    public R getReservations()
    {
        return doctorService.getReservations();
    }
    @PostMapping("/confirm")
    public R confirmReservation(@RequestBody Map<String,String> data)
    {
        return doctorService.confirmReservation(data);
    }
}
