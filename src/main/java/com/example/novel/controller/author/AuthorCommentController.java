package com.example.novel.controller.author;

import com.example.novel.common.core.Result;
import com.example.novel.service.IAuthorNovelService;
import com.example.novel.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者评论控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/author/comments")
public class AuthorCommentController {

    private final IAuthorNovelService authorNovelService;

    /**
     * 分页查询小说的评论列表
     *
     * @param novelId 小说ID
     * @param page    页码
     * @param size    每页大小
     * @param request HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CommentVO>> list(
            @RequestParam(required = false) Long novelId,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者查询评论列表，小说ID: {}, 作者ID: {}", novelId, authorUserId);
        return Result.success(authorNovelService.listComments(novelId, authorUserId, page, size));
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param novelId   小说ID
     * @param request   HTTP请求
     * @return 空结果
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> delete(@PathVariable Long commentId,
                               @RequestParam Long novelId,
                               HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者删除评论，评论ID: {}, 小说ID: {}, 作者ID: {}", commentId, novelId, authorUserId);
        authorNovelService.deleteComment(commentId, novelId, authorUserId);
        return Result.success();
    }
}
