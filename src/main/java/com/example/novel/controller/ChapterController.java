package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.service.IChapterService;
import com.example.novel.vo.ChapterDetailVO;
import com.example.novel.vo.ChapterVO;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 章节管理控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chapters")
public class ChapterController {

    private final IChapterService chapterService;

    /**
     * 分页查询章节列表
     */
    @GetMapping
    public Result<Page<ChapterVO>> listByNovelId(
            @RequestParam Long novelId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页大小不能小于1") Integer pageSize) {
        log.info("分页查询章节列表请求, novelId: {}, pageNum: {}, pageSize: {}", novelId, pageNum, pageSize);

        Page<ChapterVO> page = chapterService.listByNovelId(novelId, pageNum, pageSize);
        log.info("返回 {} 个章节，共 {} 页", page.getTotal(), page.getPages());

        return Result.success(page);
    }

    /**
     * 查询章节详情（包含内容）
     */
    @GetMapping("/{id}")
    public Result<ChapterDetailVO> getChapterById(@PathVariable Long id) {
        log.info("查询章节详情请求, chapterId: {}", id);

        ChapterDetailVO chapterDetail = chapterService.getChapterById(id);
        if (chapterDetail == null) {
            return Result.success(null);
        }

        return Result.success(chapterDetail);
    }
}