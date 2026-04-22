package com.example.novel.controller;

import com.example.novel.common.core.Result;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.common.util.JwtUtil;
import com.example.novel.dto.UserLoginDTO;
import com.example.novel.dto.UserRegisterDTO;
import com.example.novel.entity.User;
import com.example.novel.mapper.UserMapper;
import com.example.novel.service.IUserService;
import com.example.novel.vo.UserLoginVO;
import com.example.novel.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     * POST /auth/register
     */
    @PostMapping("/register")
    public Result<UserLoginVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        long startTime = System.currentTimeMillis();
        log.info("用户注册请求，用户名: {}", registerDTO.getUsername());

        try {
            UserLoginVO result = userService.register(registerDTO);
            log.info("用户注册成功，用户ID: {}", result.getUserId());
            return Result.success("注册成功", result);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info("用户注册请求耗时 {}ms", cost);
        }
    }

    /**
     * 用户登录
     * POST /auth/login
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        long startTime = System.currentTimeMillis();
        log.info("用户登录请求，用户名: {}", loginDTO.getUsername());

        try {
            UserLoginVO result = userService.login(loginDTO);
            log.info("用户登录成功，用户ID: {}", result.getUserId());
            return Result.success("登录成功", result);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info("用户登录请求耗时 {}ms", cost);
        }
    }

    /**
     * 获取当前用户信息
     * GET /auth/info
     */
    @GetMapping("/info")
    public Result<UserVO> getInfo(@RequestHeader("Authorization") String authHeader) {
        log.info("获取用户信息请求");

        // 验证token格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("请先登录");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("登录已过期，请重新登录");
        }

        // 从token中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        // 查询用户信息（确保用户仍存在且状态正常）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("该账号已被禁用");
        }

        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();

        return Result.success(userVO);
    }

    /**
     * 退出登录
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("用户退出登录请求");
        // 前端清除token即可，后端无需处理
        return Result.success("退出成功", null);
    }
}