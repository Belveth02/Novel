package com.example.novel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 作者查询章节列表DTO
 */
@Data
public class AuthorChapterQueryDTO {

    /**
     * 小说ID
     */
    private Long novelId;

    /**
     * 页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    private String sortOrder = "asc";
}
