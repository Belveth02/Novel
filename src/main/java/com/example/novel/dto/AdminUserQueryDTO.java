package com.example.novel.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员用户查询参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserQueryDTO {

    /**
     * 关键词（用户名/邮箱）
     */
    private String keyword;

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码不能小于1")
    @Builder.Default
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Builder.Default
    private Integer size = 10;
}