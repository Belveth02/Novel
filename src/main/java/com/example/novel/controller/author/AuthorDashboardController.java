package com.example.novel.controller.author;

import com.example.novel.common.core.Result;
import com.example.novel.service.IAuthorNovelService;
import com.example.novel.vo.AuthorNovelStatsVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者仪表盘控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/author/dashboard")
public class AuthorDashboardController {

    private final IAuthorNovelService authorNovelService;

    /**
     * 获取作者统计数据
     *
     * @param request HTTP请求
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<AuthorNovelStatsVO> getStats(HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("获取作者统计数据，作者ID: {}", authorUserId);
        return Result.success(authorNovelService.getStats(authorUserId));
    }
}
