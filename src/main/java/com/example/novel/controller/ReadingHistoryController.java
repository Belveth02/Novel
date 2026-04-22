package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.entity.ReadingHistory;
import com.example.novel.service.IReadingHistoryService;
import com.example.novel.vo.ReadingHistoryVO;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 阅读记录控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reading-history")
public class ReadingHistoryController {

    private final IReadingHistoryService readingHistoryService;

    /**
     * 保存或更新阅读记录
     */
    @PostMapping
    public Result<Void> saveOrUpdate(
            @RequestHeader(value = "X-User-ID", required = false) Long userId,
            @RequestBody ReadingHistory readingHistory) {
        log.info("保存阅读记录请求, userId: {}, novelId: {}, chapterId: {}",
                userId, readingHistory.getNovelId(), readingHistory.getChapterId());

        // 如果未提供用户ID，使用默认用户ID（测试用户）
        if (userId == null) {
            userId = 2L; // 测试用户ID
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        readingHistory.setUserId(userId);
        readingHistoryService.saveOrUpdate(readingHistory);
        return Result.success();
    }

    /**
     * 分页查询用户的阅读记录
     */
    @GetMapping
    public Result<Page<ReadingHistoryVO>> listByUser(
            @RequestHeader(value = "X-User-ID", required = false) Long userId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页大小不能小于1") Integer pageSize) {
        log.info("查询用户阅读记录请求, userId: {}, pageNum: {}, pageSize: {}", userId, pageNum, pageSize);

        // 如果未提供用户ID，使用默认用户ID（测试用户）
        if (userId == null) {
            userId = 2L; // 测试用户ID
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        Page<ReadingHistoryVO> page = readingHistoryService.listByUserId(userId, pageNum, pageSize);
        log.info("返回 {} 条阅读记录，共 {} 页", page.getTotal(), page.getPages());

        return Result.success(page);
    }

    /**
     * 查询用户对某本小说的阅读记录
     */
    @GetMapping("/novel/{novelId}")
    public Result<ReadingHistoryVO> getByUserAndNovel(
            @RequestHeader(value = "X-User-ID", required = false) Long userId,
            @PathVariable Long novelId) {
        log.info("查询用户小说阅读记录请求, userId: {}, novelId: {}", userId, novelId);

        // 如果未提供用户ID，使用默认用户ID（测试用户）
        if (userId == null) {
            userId = 2L; // 测试用户ID
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        ReadingHistoryVO history = readingHistoryService.getByUserAndNovel(userId, novelId);
        if (history == null) {
            return Result.success(null);
        }

        return Result.success(history);
    }
}