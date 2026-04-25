package com.example.novel.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 作者更新小说DTO
 */
@Data
public class AuthorNovelUpdateDTO {

    /**
     * 小说标题
     */
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 作者显示名称
     */
    @Size(max = 50, message = "作者名称长度不能超过50个字符")
    private String author;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 小说描述
     */
    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
}
