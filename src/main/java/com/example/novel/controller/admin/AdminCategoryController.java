package com.example.novel.controller.admin;

import com.example.novel.common.core.Result;
import com.example.novel.dto.AdminCategoryCreateDTO;
import com.example.novel.dto.AdminCategoryUpdateDTO;
import com.example.novel.service.ICategoryService;
import com.example.novel.vo.AdminCategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员分类控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final ICategoryService categoryService;

    /**
     * 获取分类列表（不分页）
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<AdminCategoryVO>> list() {
        log.info("获取分类列表");
        List<AdminCategoryVO> categories = categoryService.listAdminAll();
        return Result.success(categories);
    }

    /**
     * 新增分类
     *
     * @param createDTO 创建参数
     * @return 分类ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AdminCategoryCreateDTO createDTO) {
        log.info("新增分类，名称: {}", createDTO.getName());
        Long id = categoryService.createCategory(createDTO);
        return Result.success(id);
    }

    /**
     * 编辑分类
     *
     * @param id 分类ID
     * @param updateDTO 更新参数
     * @return 空结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AdminCategoryUpdateDTO updateDTO) {
        log.info("编辑分类，ID: {}", id);
        categoryService.updateCategory(id, updateDTO);
        return Result.success();
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 空结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除分类，ID: {}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }
}