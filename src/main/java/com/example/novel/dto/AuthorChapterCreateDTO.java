package com.example.novel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 作者创建章节DTO
 */
@Data
public class AuthorChapterCreateDTO {

    /**
     * 小说ID
     */
    @NotNull(message = "小说ID不能为空")
    private Long novelId;

    /**
     * 章节标题
     */
    @NotBlank(message = "章节标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 章节内容
     */
    @NotBlank(message = "章节内容不能为空")
    private String content;

    /**
     * 章节序号
     */
    @NotNull(message = "章节序号不能为空")
    @Min(value = 1, message = "章节序号必须大于0")
    private Integer chapterNumber;
}
