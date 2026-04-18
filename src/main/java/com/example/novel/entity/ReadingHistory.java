package com.example.novel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 阅读记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("reading_history")
public class ReadingHistory {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 小说ID
     */
    @TableField("novel_id")
    private Long novelId;

    /**
     * 章节ID
     */
    @TableField("chapter_id")
    private Long chapterId;

    /**
     * 阅读进度（百分比）
     */
    private Integer progress;

    /**
     * 最后阅读时间
     */
    @TableField("last_read_time")
    private LocalDateTime lastReadTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}