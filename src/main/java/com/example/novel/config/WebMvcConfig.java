package com.example.novel.config;

import com.example.novel.common.handler.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器，拦截所有/admin/**路径，排除登录接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/admin/**", "/api/admin/**")
                .excludePathPatterns("/admin/auth/login", "/api/admin/auth/login");
    }
}