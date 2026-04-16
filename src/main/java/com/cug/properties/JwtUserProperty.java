package com.cug.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtUserProperty {
    @Value("${bgll.jwt.user.name}")
    private String name;
    @Value("${bgll.jwt.user.expiration}")
    private Long expiration;
    @Value("${bgll.jwt.user.secret}")
    private String secret;
}
