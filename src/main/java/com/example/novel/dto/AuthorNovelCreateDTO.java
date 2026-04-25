package com.example.novel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 作者创建小说DTO
 */
@Data
public class AuthorNovelCreateDTO {

    /**
     * 小说标题
     */
    @NotBlank(message = "小说标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 作者显示名称
     */
    @NotBlank(message = "作者名称不能为空")
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
    @NotNull(message = "分类不能为空")
    private Long categoryId;
}
