package com.example.novel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员更新章节参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminChapterUpdateDTO {

    /**
     * 章节标题
     */
    private String title;

    /**
     * 章节内容
     */
    private String content;
}