package com.example.novel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员创建小说参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNovelCreateDTO {

    /**
     * 小说标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空")
    private String author;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 小说描述
     */
    private String description;

    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 状态：0-下架，1-上架
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}