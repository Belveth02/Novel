package com.example.novel.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.novel.common.core.Result;
import com.example.novel.entity.*;
import com.example.novel.mapper.*;
import com.example.novel.vo.AdminStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员仪表盘控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final UserMapper userMapper;
    private final NovelMapper novelMapper;
    private final ChapterMapper chapterMapper;
    private final CommentMapper commentMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result<AdminStatsVO> getStats() {
        log.info("获取仪表盘统计数据");

        long startTime = System.currentTimeMillis();

        // 统计用户总数（只统计状态正常的用户）
        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 1)
        );

        // 统计小说总数
        Long novelCount = novelMapper.selectCount(null);

        // 统计章节总数
        Long chapterCount = chapterMapper.selectCount(null);

        // 统计评论总数
        Long commentCount = commentMapper.selectCount(null);

        AdminStatsVO stats = AdminStatsVO.builder()
                .userCount(userCount)
                .novelCount(novelCount)
                .chapterCount(chapterCount)
                .commentCount(commentCount)
                .build();

        long cost = System.currentTimeMillis() - startTime;
        log.info("获取统计数据完成，耗时 {}ms - 用户: {}, 小说: {}, 章节: {}, 评论: {}",
                cost, userCount, novelCount, chapterCount, commentCount);

        return Result.success(stats);
    }
}
