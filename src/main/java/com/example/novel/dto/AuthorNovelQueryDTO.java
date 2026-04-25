package com.example.novel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 作者查询小说列表DTO
 */
@Data
public class AuthorNovelQueryDTO {

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
    private Integer size = 10;

    /**
     * 关键词（标题）
     */
    private String keyword;

    /**
     * 状态筛选：0-下架，1-上架
     */
    private Integer status;

    /**
     * 排序字段：create_time-创建时间，update_time-更新时间，view_count-阅读量
     */
    private String sortField = "create_time";

    /**
     * 排序方式：asc-升序，desc-降序
     */
    private String sortOrder = "desc";
}
