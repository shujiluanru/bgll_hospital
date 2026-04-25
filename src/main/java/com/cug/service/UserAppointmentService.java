package com.cug.service;

import com.cug.domain.pojo.Doctor;
import com.cug.domain.pojo.R;
import com.cug.domain.vo.DoctorInfoVO;

import java.util.List;

public interface UserAppointmentService {
    R getDoctorList();

    R getResources();

    R grab(Long doctorId);
}
