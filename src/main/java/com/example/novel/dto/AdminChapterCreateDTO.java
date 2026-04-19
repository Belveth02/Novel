package com.example.novel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员创建章节参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminChapterCreateDTO {

    /**
     * 小说ID
     */
    @NotNull(message = "小说ID不能为空")
    private Long novelId;

    /**
     * 章节标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 章节内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
}