package com.cug.config;

import com.cug.interceptor.DoctorInterceptor;
import com.cug.interceptor.UserInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
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
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/api")
                .addPathPatterns("/user/api/common/*")
                .addPathPatterns("/user/api/ai/chat")
                .addPathPatterns("/user/api/ai/chat/*")
                .addPathPatterns("/user/api/appointment/*")
                .excludePathPatterns("/user/api/login/*",
                        "/user/api/register",
                        "/user/api/send-code");
        registry.addInterceptor(doctorInterceptor)
                .addPathPatterns("/doctor/api")
                .addPathPatterns("/doctor/api/common/*")
                .excludePathPatterns("/doctor/api/login");
    }
}
