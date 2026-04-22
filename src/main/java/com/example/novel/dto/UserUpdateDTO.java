package com.example.novel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新DTO
 */
@Data
public class UserUpdateDTO {

    /**
     * 昵称
     */
    @Size(min = 2, max = 20, message = "昵称长度为2-20个字符")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "请输入正确的邮箱格式")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号格式")
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;
}
