package com.cug.config;

import com.cug.interceptor.DoctorInterceptor;
import com.cug.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserInterceptor userInterceptor;
    private final DoctorInterceptor doctorInterceptor;

    public WebConfig(UserInterceptor userInterceptor, DoctorInterceptor doctorInterceptor) {
        this.userInterceptor = userInterceptor;
        this.doctorInterceptor = doctorInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 用户端拦截器 - 拦截 /user/api/ 下的所有请求
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/api/**")           // ✅ 用 /** 匹配所有子路径
                .excludePathPatterns(
                        "/user/api/login/**",              // 登录放行
                        "/user/api/register",              // 注册放行
                        "/user/api/send-code"              // 验证码放行
                                    );

        // 医生端拦截器 - 拦截 /doctor/api/ 下的所有请求
        registry.addInterceptor(doctorInterceptor)
                .addPathPatterns("/doctor/api/**")         // ✅ 用 /** 匹配所有子路径
                .excludePathPatterns(
                        "/doctor/api/login"
                );
    }
}