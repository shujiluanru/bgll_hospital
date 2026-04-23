package com.cug.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtDoctorProperty {
    @Value("${bgll.jwt.doctor.name}")
    private String name;
    @Value("${bgll.jwt.doctor.expiration}")
    private Long expiration;
    @Value("${bgll.jwt.doctor.secret}")
    private String secret;
}
