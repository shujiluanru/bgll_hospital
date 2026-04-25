package com.cug.interceptor;

import com.cug.context.UserContext;
import com.cug.exception.ValidationException;
import com.cug.properties.JwtUserProperty;
import com.cug.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    private final JwtUserProperty jwtUserProperty;
    @Autowired
    public UserInterceptor(JwtUserProperty jwtUserProperty) {
        this.jwtUserProperty = jwtUserProperty;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(jwtUserProperty.getName());
        if (header == null) {
            log.info("请求头没有token");
            throw new ValidationException("");
        }
        String[]splits = header.split(" ");
        String token = splits[1];
        if (token == null) {
            throw new ValidationException("");
        }
        try {
            Claims claims = JwtUtil.parseToken(token, jwtUserProperty.getSecret());
            Object id = claims.get("id");
            //转Long存入threadlocal
            UserContext.setUserId(Long.parseLong(id.toString()));
        } catch (Exception e) {
            throw new ValidationException("");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.removeUserId();
    }
}
