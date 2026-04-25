package com.example.novel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者小说统计数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorNovelStatsVO {

    /**
     * 小说总数
     */
    private Integer totalNovels;

    /**
     * 章节总数
     */
    private Integer totalChapters;

    /**
     * 总字数
     */
    private Integer totalWordCount;

    /**
     * 总阅读量
     */
    private Integer totalViewCount;

    /**
     * 总评论数
     */
    private Integer totalComments;

    /**
     * 上架小说数
     */
    private Integer publishedNovels;

    /**
     * 下架小说数
     */
    private Integer unpublishedNovels;
}
