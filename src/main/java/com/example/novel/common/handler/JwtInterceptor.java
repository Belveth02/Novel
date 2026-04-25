package com.example.novel.common.handler;

import com.example.novel.common.constant.RoleConstant;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.info("JWT拦截器处理请求: {} {}", method, requestURI);

        // 排除登录接口
        if (requestURI.startsWith("/api/admin/auth/login") || requestURI.startsWith("/api/author/auth/login")) {
            log.info("放行登录接口: {}", requestURI);
            return true;
        }

        // 获取Authorization头
        String authHeader = request.getHeader(jwtUtil.getHeader());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求缺少有效的Authorization头: {}", requestURI);
            throw new BusinessException("请先登录");
        }

        String token = authHeader.substring(7);

        // 作者后台接口单独处理
        if (requestURI.startsWith("/api/author/")) {
            return handleAuthorRequest(request, response, token);
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("JWT token无效或已过期: {}", requestURI);
            throw new BusinessException("登录已过期，请重新登录");
        }

        // 验证用户角色（必须是管理员）
        String role = jwtUtil.getRoleFromToken(token);
        if (!RoleConstant.ADMIN.equals(role)) {
            log.warn("用户角色不是管理员，禁止访问: {}", requestURI);
            throw new BusinessException("权限不足，仅限管理员访问");
        }

        // 将用户ID存入请求属性，供后续使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", role);

        log.debug("JWT验证通过，用户ID: {}, 角色: {}, 请求: {}", userId, role, requestURI);
        return true;
    }

    /**
     * 处理作者后台接口请求
     */
    private boolean handleAuthorRequest(HttpServletRequest request, HttpServletResponse response, String token) throws Exception {
        String requestURI = request.getRequestURI();

        if (!jwtUtil.validateToken(token)) {
            log.warn("JWT token无效或已过期: {}", requestURI);
            throw new BusinessException("登录已过期，请重新登录");
        }

        // 验证用户角色（必须是作者或管理员）
        String role = jwtUtil.getRoleFromToken(token);
        if (!RoleConstant.AUTHOR.equals(role) && !RoleConstant.ADMIN.equals(role)) {
            log.warn("用户角色不是作者，禁止访问作者后台: {}", requestURI);
            throw new BusinessException("权限不足，仅限作者访问");
        }

        // 将用户ID存入请求属性，供后续使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", role);

        log.debug("作者后台JWT验证通过，用户ID: {}, 角色: {}, 请求: {}", userId, role, requestURI);
        return true;
    }
}