package com.cug.domain.vo;

import lombok.Data;

@Data
public class DoctorInfoForAI {
    private String name;
    private String departmentName;
    private String description;
    private Long resourceNum;
    private String phone;
}
