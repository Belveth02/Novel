package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.service.ICommentService;
import com.example.novel.vo.CommentVO;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final ICommentService commentService;

    /**
     * 发布评论
     */
    @PostMapping
    public Result<Long> addComment(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr,
            @RequestParam Long novelId,
            @RequestParam String content) {
        log.info("发布评论请求, userId: {}, novelId: {}, content: {}", userIdStr, novelId, content);

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

        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            log.warn("评论内容不能为空");
            return Result.error("评论内容不能为空");
        }

        Long commentId = commentService.addComment(userId, novelId, content);
        log.info("评论发布成功, commentId: {}, userId: {}, novelId: {}", commentId, userId, novelId);

        return Result.success(commentId);
    }

    /**
     * 分页查询小说的评论列表
     */
    @GetMapping
    public Result<Page<CommentVO>> listByNovel(
            @RequestParam Long novelId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页大小不能小于1") Integer pageSize) {
        log.info("查询小说评论列表请求, novelId: {}, pageNum: {}, pageSize: {}", novelId, pageNum, pageSize);

        Page<CommentVO> page = commentService.listByNovelId(novelId, pageNum, pageSize);
        log.info("返回 {} 条评论，共 {} 页", page.getTotal(), page.getPages());

        return Result.success(page);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Boolean> deleteComment(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr,
            @PathVariable Long commentId) {
        log.info("删除评论请求, userId: {}, commentId: {}", userIdStr, commentId);

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

        boolean success = commentService.deleteComment(userId, commentId);
        if (success) {
            log.info("评论删除成功, commentId: {}, userId: {}", commentId, userId);
            return Result.success(true);
        } else {
            log.warn("评论删除失败, commentId: {}, userId: {}", commentId, userId);
            return Result.error("评论删除失败");
        }
    }

    /**
     * 统计小说的评论数量
     */
    @GetMapping("/count")
    public Result<Long> countByNovel(@RequestParam Long novelId) {
        log.info("统计小说评论数量请求, novelId: {}", novelId);

        Long count = commentService.countByNovelId(novelId);
        log.info("小说评论数量: {}, novelId: {}", count, novelId);

        return Result.success(count);
    }

    /**
     * 获取当前用户的评论列表
     */
    @GetMapping("/my")
    public Result<Page<CommentVO>> listMyComments(
            @RequestHeader(value = "X-User-ID", required = false) String userIdStr,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小不能小于1") Integer pageSize) {
        log.info("查询我的评论列表请求, userId: {}, pageNum: {}, pageSize: {}", userIdStr, pageNum, pageSize);

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

        Page<CommentVO> page = commentService.listByUserId(userId, pageNum, pageSize);
        log.info("返回 {} 条评论，共 {} 页", page.getTotal(), page.getPages());

        return Result.success(page);
    }
}