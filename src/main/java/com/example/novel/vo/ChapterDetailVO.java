package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 章节详情视图对象（包含内容）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    private Long id;

    /**
     * 小说ID
     */
    private Long novelId;

    /**
     * 小说标题
     */
    private String novelTitle;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 章节内容
     */
    private String content;

    /**
     * 章节序号
     */
    private Integer chapterNumber;

    /**
     * 章节字数
     */
    private Integer wordCount;

    /**
     * 上一章ID（可选）
     */
    private Long prevChapterId;

    /**
     * 下一章ID（可选）
     */
    private Long nextChapterId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}