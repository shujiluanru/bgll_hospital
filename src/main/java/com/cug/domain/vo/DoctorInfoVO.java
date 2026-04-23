package com.cug.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorInfoVO {
    private String departmentName;
    private String phone;
    private String email;
    private String name;
    private String description;
}
