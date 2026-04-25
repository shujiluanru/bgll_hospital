package com.cug.interceptor;

import com.cug.context.DoctorContext;
import com.cug.context.UserContext;
import com.cug.exception.ValidationException;
import com.cug.properties.JwtDoctorProperty;
import com.cug.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.netty.util.Recycler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class DoctorInterceptor implements HandlerInterceptor {
    private final JwtDoctorProperty jwtDoctorProperty;
    public DoctorInterceptor(JwtDoctorProperty jwtDoctorProperty) {
        this.jwtDoctorProperty = jwtDoctorProperty;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //静态资源放行
        //从请求头获取 token
        String header = request.getHeader(jwtDoctorProperty.getName());
        if (header == null) {
            log.info("请求头没有token");
            throw new ValidationException("");
        }
        String[]splits = header.split(" ");
        String token = splits[1];
        log.info("拦截请求DoctorInterceptor");
        if (token == null) {
            throw new ValidationException("");
        }
        try {
            Claims claims = JwtUtil.parseToken(token, jwtDoctorProperty.getSecret());
            Object id = claims.get("id");
            //转Long存入threadlocal
            DoctorContext.setDoctorId(Long.parseLong(id.toString()));
        } catch (Exception e) {
            throw new ValidationException("");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        DoctorContext.removeDoctorId();
    }
}
