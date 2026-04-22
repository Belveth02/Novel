package com.example.novel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 安全相关配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 提供BCrypt密码编码器
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离项目使用JWT无需CSRF）
            .csrf(AbstractHttpConfigurer::disable)
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置会话管理为无状态（使用JWT）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置权限
            .authorizeHttpRequests(auth -> auth
                // 放行用户认证接口
                .requestMatchers("/auth/**").permitAll()
                // 放行管理员登录接口
                .requestMatchers("/admin/auth/**").permitAll()
                // 放行所有小说相关公开接口
                .requestMatchers("/novels/**").permitAll()
                .requestMatchers("/chapters/**").permitAll()
                // 放行分类公开接口
                .requestMatchers("/categories/**").permitAll()
                // 放行评论公开接口（列表和数量查询）
                .requestMatchers("/comments/**").permitAll()
                // 放行收藏公开接口（列表和数量查询）
                .requestMatchers("/favorites/**").permitAll()
                // 放行阅读历史公开接口
                .requestMatchers("/reading-history/**").permitAll()
                // 放行静态资源和首页
                .requestMatchers("/", "/favicon.ico", "/static/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated())
            // 禁用表单登录
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用HTTP Basic
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 配置CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}