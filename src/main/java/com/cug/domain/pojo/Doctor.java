package com.cug.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String description;

}
