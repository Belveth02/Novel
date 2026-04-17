package com.example.novel.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小说查询参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelQueryDTO {

    /**
     * 分类ID（可选）
     */
    private Long categoryId;

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码不能小于1")
    @lombok.Builder.Default
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @lombok.Builder.Default
    private Integer pageSize = 10;

    /**
     * 排序字段：view_count-阅读量，create_time-创建时间，默认view_count
     */
    @lombok.Builder.Default
    private String sortField = "view_count";

    /**
     * 排序方向：asc-升序，desc-降序，默认desc
     */
    @lombok.Builder.Default
    private String sortOrder = "desc";
}