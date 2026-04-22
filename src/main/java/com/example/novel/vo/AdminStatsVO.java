package com.example.novel.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 管理员仪表盘统计数据VO
 */
@Data
@Builder
public class AdminStatsVO {

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 小说总数
     */
    private Long novelCount;

    /**
     * 章节总数
     */
    private Long chapterCount;

    /**
     * 评论总数
     */
    private Long commentCount;
}
