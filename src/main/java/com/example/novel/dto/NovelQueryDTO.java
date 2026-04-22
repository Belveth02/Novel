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
     * 小说标题（可选）
     */
    private String title;

    /**
     * 作者（可选）
     */
    private String author;

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
    private Integer size = 12;

    /**
     * 排序字段：viewCount-阅读量，createTime-创建时间，wordCount-字数，默认viewCount
     */
    @lombok.Builder.Default
    private String sortBy = "viewCount";

    /**
     * 排序方向：asc-升序，desc-降序，默认desc
     */
    @lombok.Builder.Default
    private String sortOrder = "desc";

    /**
     * 获取数据库排序字段（将前端字段名转换为数据库字段名）
     */
    public String getSortField() {
        if ("viewCount".equals(sortBy)) {
            return "view_count";
        } else if ("createTime".equals(sortBy)) {
            return "create_time";
        } else if ("wordCount".equals(sortBy)) {
            return "word_count";
        }
        return "view_count";
    }
}