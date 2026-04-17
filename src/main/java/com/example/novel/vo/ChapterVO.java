package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 章节展示视图对象（列表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterVO implements Serializable {

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
     * 章节标题
     */
    private String title;

    /**
     * 章节序号
     */
    private Integer chapterNumber;

    /**
     * 章节字数
     */
    private Integer wordCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}