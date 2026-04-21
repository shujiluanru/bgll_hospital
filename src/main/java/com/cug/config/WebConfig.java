package com.cug.config;

import com.cug.interceptor.UserInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserInterceptor userInterceptor;
    public WebConfig(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/api")
                .excludePathPatterns("/user/api/login/*",
                        "/user/api/register",
                        "/user/api/send-code",
                        "/user/api/ai/chat/*");
    }
}
