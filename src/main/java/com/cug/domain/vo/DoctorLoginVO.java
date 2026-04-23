package com.cug.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorLoginVO {
    private String token;
    private DoctorInfoVO doctorInfo;
}
