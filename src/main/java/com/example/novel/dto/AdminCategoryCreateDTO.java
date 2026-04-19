package com.example.novel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员创建分类参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryCreateDTO {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String name;

    /**
     * 分类描述
     */
    @Size(max = 200, message = "分类描述不能超过200个字符")
    private String description;

    /**
     * 排序序号
     */
    @Builder.Default
    private Integer sortOrder = 0;
}