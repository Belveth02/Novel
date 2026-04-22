package com.example.novel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员更新小说参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNovelUpdateDTO {

    /**
     * 小说标题
     */
    private String title;

    /**
     * 作者
     */
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
    private Long categoryId;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 是否热门推荐：0-否，1-是
     */
    private Integer isHot;

    /**
     * 热门排序（数值越大越靠前）
     */
    private Integer hotSort;
}