package com.cug.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoVO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String sex;
    private Integer age;
}
