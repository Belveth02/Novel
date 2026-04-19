package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.dto.NovelQueryDTO;
import com.example.novel.service.INovelService;
import com.example.novel.vo.NovelVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 小说管理控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/novels")
public class NovelController {

    private final INovelService novelService;

    /**
     * 分页查询小说列表
     */
    @GetMapping
    public Result<Page<NovelVO>> listByPage(@Valid NovelQueryDTO queryDTO) {
        long startTime = System.currentTimeMillis();
        log.info("分页查询小说列表请求, 参数: {}", queryDTO);

        try {
            Page<NovelVO> page = novelService.listByPage(queryDTO);
            log.info("返回 {} 条记录，共 {} 页", page.getTotal(), page.getPages());
            return Result.success(page);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info("分页查询小说列表请求耗时 {}ms", cost);
        }
    }

    /**
     * 获取小说详情
     */
    @GetMapping("/{id}")
    public Result<NovelVO> getById(@PathVariable Long id) {
        log.info("获取小说详情请求, ID: {}", id);

        NovelVO novelVO = novelService.getById(id);
        if (novelVO == null) {
            log.warn("小说不存在或已下架, ID: {}", id);
            return Result.error("小说不存在或已下架");
        }

        log.info("返回小说详情: {}", novelVO.getTitle());
        return Result.success(novelVO);
    }

    /**
     * 获取热门小说推荐
     */
    @GetMapping("/hot")
    public Result<Page<NovelVO>> listHotNovels(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门小说推荐请求, limit: {}", limit);

        if (limit <= 0 || limit > 100) {
            log.warn("推荐数量参数无效: {}", limit);
            return Result.error("推荐数量必须在1-100之间");
        }

        Page<NovelVO> page = novelService.listHotNovels(limit);
        log.info("返回 {} 本热门小说", page.getRecords().size());

        return Result.success(page);
    }
}