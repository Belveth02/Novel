package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginVO {

    /**
     * JWT token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 角色
     */
    private String role;

    /**
     * 头像URL
     */
    private String avatar;
}