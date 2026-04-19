package com.example.novel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员章节查询参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminChapterQueryDTO {

    /**
     * 小说ID
     */
    @NotNull(message = "小说ID不能为空")
    private Long novelId;

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
}