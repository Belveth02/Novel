package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.service.IFavoriteService;
import com.example.novel.vo.FavoriteVO;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final IFavoriteService favoriteService;

    /**
     * 收藏/取消收藏
     */
    @PostMapping("/toggle")
    public Result<Boolean> toggleFavorite(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr,
            @RequestParam Long novelId) {
        log.info("收藏/取消收藏请求, userId: {}, novelId: {}", userIdStr, novelId);

        Long userId = 2L; // 默认测试用户ID

        // 解析用户ID
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                userId = Long.parseLong(userIdStr.trim());
            } catch (NumberFormatException e) {
                log.warn("用户ID格式无效: {}, 使用默认用户ID: {}", userIdStr, userId);
            }
        } else {
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        boolean isFavorited = favoriteService.toggleFavorite(userId, novelId);
        String action = isFavorited ? "收藏" : "取消收藏";
        log.info("{}成功, userId: {}, novelId: {}", action, userId, novelId);

        return Result.success(isFavorited);
    }

    /**
     * 检查收藏状态
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr,
            @RequestParam Long novelId) {
        log.info("检查收藏状态请求, userId: {}, novelId: {}", userIdStr, novelId);

        Long userId = 2L; // 默认测试用户ID

        // 解析用户ID
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                userId = Long.parseLong(userIdStr.trim());
            } catch (NumberFormatException e) {
                log.warn("用户ID格式无效: {}, 使用默认用户ID: {}", userIdStr, userId);
            }
        } else {
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        boolean isFavorited = favoriteService.isFavorite(userId, novelId);
        log.info("收藏状态: {}, userId: {}, novelId: {}", isFavorited ? "已收藏" : "未收藏", userId, novelId);

        return Result.success(isFavorited);
    }

    /**
     * 分页查询用户的收藏列表
     */
    @GetMapping
    public Result<Page<FavoriteVO>> listByUser(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页大小不能小于1") Integer pageSize) {
        log.info("查询用户收藏列表请求, userId: {}, pageNum: {}, pageSize: {}", userIdStr, pageNum, pageSize);

        Long userId = 2L; // 默认测试用户ID

        // 解析用户ID
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                userId = Long.parseLong(userIdStr.trim());
            } catch (NumberFormatException e) {
                log.warn("用户ID格式无效: {}, 使用默认用户ID: {}", userIdStr, userId);
            }
        } else {
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        Page<FavoriteVO> page = favoriteService.listByUserId(userId, pageNum, pageSize);
        log.info("返回 {} 条收藏记录，共 {} 页", page.getTotal(), page.getPages());

        return Result.success(page);
    }

    /**
     * 统计用户的收藏数量
     */
    @GetMapping("/count")
    public Result<Long> countByUser(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr) {
        log.info("统计用户收藏数量请求, userId: {}", userIdStr);

        Long userId = 2L; // 默认测试用户ID

        // 解析用户ID
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                userId = Long.parseLong(userIdStr.trim());
            } catch (NumberFormatException e) {
                log.warn("用户ID格式无效: {}, 使用默认用户ID: {}", userIdStr, userId);
            }
        } else {
            log.info("未提供用户ID，使用默认用户ID: {}", userId);
        }

        Long count = favoriteService.countByUserId(userId);
        log.info("用户收藏数量: {}", count);

        return Result.success(count);
    }
}