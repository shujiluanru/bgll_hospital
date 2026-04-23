package com.cug.service;

import com.cug.domain.dto.DoctorLoginDTO;
import com.cug.domain.pojo.R;

public interface DoctorAuthService {
    R login(DoctorLoginDTO doctorLoginDTO);
}
