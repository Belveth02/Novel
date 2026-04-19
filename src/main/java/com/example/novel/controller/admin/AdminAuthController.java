package com.example.novel.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.novel.common.core.Result;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.common.util.JwtUtil;
import com.example.novel.dto.AdminLoginDTO;
import com.example.novel.entity.User;
import com.example.novel.mapper.UserMapper;
import com.example.novel.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Validated @RequestBody AdminLoginDTO loginDTO) {
        log.info("管理员登录请求，用户名: {}", loginDTO.getUsername());

        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() != 1) {
            log.warn("登录失败，用户已被禁用: {}", loginDTO.getUsername());
            throw new BusinessException("用户已被禁用");
        }

        // 验证用户角色（必须是管理员）
        if (!"ADMIN".equals(user.getRole())) {
            log.warn("登录失败，用户不是管理员: {}", loginDTO.getUsername());
            throw new BusinessException("权限不足，仅限管理员登录");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败，密码错误: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建响应
        AdminLoginVO loginVO = AdminLoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();

        log.info("管理员登录成功，用户ID: {}, 角色: {}", user.getId(), user.getRole());
        return Result.success(loginVO);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<AdminLoginVO> getInfo(@RequestHeader("Authorization") String authHeader) {
        log.info("获取管理员信息请求");

        // 验证token格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("无效的认证信息");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("认证已过期或无效");
        }

        // 从token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        // 查询用户信息（确保用户仍存在且状态正常）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException("权限不足");
        }

        AdminLoginVO loginVO = AdminLoginVO.builder()
                .token(token) // 返回原token
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();

        return Result.success(loginVO);
    }
}