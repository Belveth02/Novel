package com.example.novel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS跨域配置类
 * 用于解决前端请求跨域问题
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS过滤器
     * 允许所有域名访问（开发环境）
     * 允许所有请求方法和请求头
     * 允许携带凭证
     */
    @Bean
    public CorsFilter corsFilter() {
        // 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有域名访问（生产环境应指定具体域名）
        config.addAllowedOriginPattern("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法（GET、POST、PUT、DELETE等）
        config.addAllowedMethod("*");

        // 允许携带凭证（如cookies、authorization headers等）
        config.setAllowCredentials(true);

        // 设置预检请求的有效期（单位：秒）
        // 在这个时间内，同一请求不需要再次发送预检请求
        config.setMaxAge(3600L);

        // 创建基于URL的CORS配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}