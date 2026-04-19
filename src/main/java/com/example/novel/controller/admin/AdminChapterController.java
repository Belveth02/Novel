package com.example.novel.controller.admin;

import com.example.novel.common.core.Result;
import com.example.novel.dto.AdminChapterCreateDTO;
import com.example.novel.dto.AdminChapterQueryDTO;
import com.example.novel.dto.AdminChapterUpdateDTO;
import com.example.novel.service.IChapterService;
import com.example.novel.vo.ChapterDetailVO;
import com.example.novel.vo.ChapterVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员章节控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/chapters")
public class AdminChapterController {

    private final IChapterService chapterService;

    /**
     * 分页查询章节列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChapterVO>> list(@Valid AdminChapterQueryDTO queryDTO) {
        log.info("查询章节列表，参数: {}", queryDTO);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChapterVO> page = chapterService.listAdminByPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取章节详情
     *
     * @param id 章节ID
     * @return 章节详情
     */
    @GetMapping("/{id}")
    public Result<ChapterDetailVO> get(@PathVariable Long id) {
        log.info("获取章节详情，ID: {}", id);
        ChapterDetailVO chapter = chapterService.getAdminById(id);
        return Result.success(chapter);
    }

    /**
     * 新增章节
     *
     * @param createDTO 创建参数
     * @return 章节ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AdminChapterCreateDTO createDTO) {
        long startTime = System.currentTimeMillis();
        log.info("新增章节，标题: {}", createDTO.getTitle());
        try {
            Long id = chapterService.createChapter(createDTO);
            return Result.success(id);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info("新增章节请求耗时 {}ms", cost);
        }
    }

    /**
     * 编辑章节
     *
     * @param id 章节ID
     * @param updateDTO 更新参数
     * @return 空结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AdminChapterUpdateDTO updateDTO) {
        log.info("编辑章节，ID: {}", id);
        chapterService.updateChapter(id, updateDTO);
        return Result.success();
    }

    /**
     * 删除章节
     *
     * @param id 章节ID
     * @return 空结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除章节，ID: {}", id);
        chapterService.deleteChapter(id);
        return Result.success();
    }
}