package com.cug.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    public static String generateToken(Claims claims, String secret, Long expire) {
        SecretKey key= Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key,io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }
    public static Claims parseToken(String token, String secret) {
        SecretKey key= Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
