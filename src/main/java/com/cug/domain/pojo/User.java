package com.cug.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private Long id;
    private String phone;
    private String email;
    private Integer age;
    private String sex;
}
