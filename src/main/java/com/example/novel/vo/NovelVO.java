package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 小说展示视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Integer wordCount;

    /**
     * 章节数
     */
    private Integer chapterCount;

    /**
     * 阅读次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}