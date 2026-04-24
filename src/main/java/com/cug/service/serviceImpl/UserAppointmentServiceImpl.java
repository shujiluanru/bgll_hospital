package com.cug.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.cug.domain.pojo.Doctor;
import com.cug.domain.pojo.R;
import com.cug.domain.vo.DoctorInfoVO;
import com.cug.service.DoctorService;
import com.cug.service.UserAppointmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAppointmentServiceImpl implements UserAppointmentService {
    private final DoctorService doctorService;
    public UserAppointmentServiceImpl(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public R getDoctorList() {
        List<Doctor> doctorList = doctorService.getDoctorList();
        List<DoctorInfoVO> doctorInfoVOList = BeanUtil.copyToList(doctorList, DoctorInfoVO.class);
        return R.ok(doctorInfoVOList);
    }
}
