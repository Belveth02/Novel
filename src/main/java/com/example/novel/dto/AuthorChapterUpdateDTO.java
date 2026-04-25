package com.example.novel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 作者更新章节DTO
 */
@Data
public class AuthorChapterUpdateDTO {

    /**
     * 章节标题
     */
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 章节内容
     */
    private String content;

    /**
     * 章节序号
     */
    @Min(value = 1, message = "章节序号必须大于0")
    private Integer chapterNumber;
}
