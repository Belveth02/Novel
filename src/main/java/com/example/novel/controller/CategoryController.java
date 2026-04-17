package com.example.novel.controller;

import com.example.novel.common.core.Result;
import com.example.novel.service.ICategoryService;
import com.example.novel.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类管理控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping
    public Result<List<CategoryVO>> listAll() {
        log.info("获取分类列表请求");

        List<CategoryVO> categories = categoryService.listAll();
        log.info("返回 {} 个分类", categories.size());

        return Result.success(categories);
    }
}