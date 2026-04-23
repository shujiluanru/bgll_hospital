package com.cug.service;

import com.cug.domain.pojo.R;

import java.util.Map;

public interface DoctorService {
    R updateDescription(Map<String,String>data);

    R getReleaseLog();

    R release(Map<String, String> data);
}
