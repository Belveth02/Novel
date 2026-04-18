package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 小说ID
     */
    private Long novelId;

    /**
     * 小说标题
     */
    private String novelTitle;

    /**
     * 小说封面
     */
    private String novelCoverImage;

    /**
     * 小说作者
     */
    private String novelAuthor;

    /**
     * 小说描述
     */
    private String novelDescription;

    /**
     * 小说字数
     */
    private Integer novelWordCount;

    /**
     * 小说章节数
     */
    private Integer novelChapterCount;

    /**
     * 小说阅读次数
     */
    private Integer novelViewCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}