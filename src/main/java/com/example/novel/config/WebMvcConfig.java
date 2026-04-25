package com.example.novel.config;

import com.example.novel.common.handler.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Value("${app.upload.avatar-path:uploads/avatars/}")
    private String avatarUploadPath;

    @Value("${app.upload.avatar-url:/uploads/avatars/}")
    private String avatarUrlPrefix;

    @Value("${app.upload.cover-path:uploads/covers/}")
    private String coverUploadPath;

    @Value("${app.upload.cover-url:/uploads/covers/}")
    private String coverUrlPrefix;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器，拦截所有/admin/**路径，排除登录接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/admin/**", "/api/admin/**")
                .excludePathPatterns("/admin/auth/login", "/api/admin/auth/login");

        // 注册JWT拦截器，拦截所有/author/**路径，排除登录接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/author/**", "/api/author/**")
                .excludePathPatterns("/author/auth/login", "/api/author/auth/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像访问路径映射
        // 注意：context-path=/api 会被 Spring 自动处理，这里只需要配置去掉 /api 后的路径
        registry.addResourceHandler(avatarUrlPrefix + "**")
                .addResourceLocations("file:" + avatarUploadPath);

        // 配置封面访问路径映射
        registry.addResourceHandler(coverUrlPrefix + "**")
                .addResourceLocations("file:" + coverUploadPath);
    }
}