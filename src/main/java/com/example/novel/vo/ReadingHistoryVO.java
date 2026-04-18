package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 阅读记录视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
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
     * 章节ID
     */
    private Long chapterId;

    /**
     * 章节标题
     */
    private String chapterTitle;

    /**
     * 章节序号
     */
    private Integer chapterNumber;

    /**
     * 阅读进度（百分比）
     */
    private Integer progress;

    /**
     * 最后阅读时间
     */
    private LocalDateTime lastReadTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}