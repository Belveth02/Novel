package com.example.novel.controller;

import com.example.novel.common.core.Result;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.common.util.JwtUtil;
import com.example.novel.dto.UserUpdateDTO;
import com.example.novel.dto.ChangePasswordDTO;
import com.example.novel.entity.User;
import com.example.novel.mapper.UserMapper;
import com.example.novel.service.IUserService;
import com.example.novel.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /**
     * 获取当前登录用户信息
     * GET /users/me
     */
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        log.info("获取当前用户信息请求");

        Long userId = getUserIdFromToken(authHeader);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = convertToVO(user);
        return Result.success(userVO);
    }

    /**
     * 更新当前用户信息
     * PUT /users/me
     */
    @PutMapping("/me")
    public Result<UserVO> updateCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("更新当前用户信息请求");

        Long userId = getUserIdFromToken(authHeader);

        UserVO updatedUser = userService.updateUser(userId, updateDTO);
        log.info("用户信息更新成功, userId: {}", userId);

        return Result.success("更新成功", updatedUser);
    }

    /**
     * 修改密码
     * POST /users/me/password
     */
    @PostMapping("/me/password")
    public Result<Boolean> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordDTO passwordDTO) {
        log.info("修改密码请求");

        Long userId = getUserIdFromToken(authHeader);

        boolean success = userService.changePassword(userId, passwordDTO);
        if (success) {
            log.info("密码修改成功, userId: {}", userId);
            return Result.success("密码修改成功", true);
        } else {
            log.warn("密码修改失败, userId: {}", userId);
            return Result.error("密码修改失败");
        }
    }

    /**
     * 上传头像
     * POST /users/me/avatar
     */
    @PostMapping("/me/avatar")
    public Result<String> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        log.info("上传头像请求");

        Long userId = getUserIdFromToken(authHeader);

        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        // 验证文件大小（限制2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过2MB");
        }

        String avatarUrl = userService.uploadAvatar(userId, file);
        log.info("头像上传成功, userId: {}", userId);

        return Result.success("上传成功", avatarUrl);
    }

    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("请先登录");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("登录已过期，请重新登录");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException("无效的用户信息");
        }

        return userId;
    }

    /**
     * 将User实体转换为UserVO
     */
    private UserVO convertToVO(User user) {
        return UserVO.builder()
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
    }
}
