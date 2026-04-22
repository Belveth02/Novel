package com.example.novel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 小说实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("novel")
public class Novel {

    /**
     * 小说ID
     */
    private Long id;

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
    @TableField("cover_image")
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
     * 总字数
     */
    @TableField("word_count")
    private Integer wordCount;

    /**
     * 章节数
     */
    @TableField("chapter_count")
    private Integer chapterCount;

    /**
     * 阅读次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 是否热门推荐：0-否，1-是
     */
    @TableField("is_hot")
    private Integer isHot;

    /**
     * 热门排序（数值越大越靠前）
     */
    @TableField("hot_sort")
    private Integer hotSort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}