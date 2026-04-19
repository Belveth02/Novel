package com.example.novel.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员小说查询参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNovelQueryDTO {

    /**
     * 关键词（标题/作者）
     */
    private String keyword;

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码不能小于1")
    @lombok.Builder.Default
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @lombok.Builder.Default
    private Integer size = 10;

    /**
     * 分类ID（可选）
     */
    private Long categoryId;

    /**
     * 状态：0-下架，1-上架（可选）
     */
    private Integer status;
}