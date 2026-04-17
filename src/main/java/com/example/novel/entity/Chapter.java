package com.example.novel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 章节实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("chapter")
public class Chapter {

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
     * 章节内容
     */
    private String content;

    /**
     * 章节序号
     */
    @TableField("chapter_number")
    private Integer chapterNumber;

    /**
     * 章节字数
     */
    @TableField("word_count")
    private Integer wordCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}