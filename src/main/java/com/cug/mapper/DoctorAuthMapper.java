package com.cug.mapper;

import com.cug.domain.pojo.Doctor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorAuthMapper {
    Doctor getDoctorByPhone(String phone);
}
