package com.cug.service;

import com.cug.domain.pojo.R;

import java.util.List;
import java.util.Map;
import com.cug.domain.pojo.Doctor;
public interface DoctorService {
    R updateDescription(Map<String,String>data);

    R getReleaseLog();

    R release(Map<String, String> data);

    List<Doctor> getDoctorList();
}
