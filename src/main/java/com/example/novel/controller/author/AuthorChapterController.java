package com.example.novel.controller.author;

import com.example.novel.common.core.Result;
import com.example.novel.dto.AuthorChapterCreateDTO;
import com.example.novel.dto.AuthorChapterQueryDTO;
import com.example.novel.dto.AuthorChapterUpdateDTO;
import com.example.novel.service.IAuthorNovelService;
import com.example.novel.vo.ChapterDetailVO;
import com.example.novel.vo.ChapterVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者章节控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/author/chapters")
public class AuthorChapterController {

    private final IAuthorNovelService authorNovelService;

    /**
     * 分页查询小说的章节列表
     *
     * @param queryDTO 查询参数
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChapterVO>> list(
            @Valid AuthorChapterQueryDTO queryDTO,
            HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者查询章节列表，小说ID: {}, 作者ID: {}", queryDTO.getNovelId(), authorUserId);
        return Result.success(authorNovelService.listChapters(queryDTO.getNovelId(), authorUserId, queryDTO));
    }

    /**
     * 获取章节详情
     *
     * @param id      章节ID
     * @param request HTTP请求
     * @return 章节详情
     */
    @GetMapping("/{id}")
    public Result<ChapterDetailVO> get(@PathVariable Long id, HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者获取章节详情，ID: {}, 作者ID: {}", id, authorUserId);
        return Result.success(authorNovelService.getChapterById(id, authorUserId));
    }

    /**
     * 创建章节
     *
     * @param createDTO 创建参数
     * @param request   HTTP请求
     * @return 章节ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AuthorChapterCreateDTO createDTO, HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者创建章节，小说ID: {}, 章节号: {}, 作者ID: {}",
                createDTO.getNovelId(), createDTO.getChapterNumber(), authorUserId);
        Long id = authorNovelService.createChapter(authorUserId, createDTO);
        return Result.success(id);
    }

    /**
     * 更新章节
     *
     * @param id        章节ID
     * @param updateDTO 更新参数
     * @param request   HTTP请求
     * @return 空结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody AuthorChapterUpdateDTO updateDTO,
                               HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者更新章节，ID: {}, 作者ID: {}", id, authorUserId);
        authorNovelService.updateChapter(id, authorUserId, updateDTO);
        return Result.success();
    }

    /**
     * 删除章节
     *
     * @param id      章节ID
     * @param request HTTP请求
     * @return 空结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者删除章节，ID: {}, 作者ID: {}", id, authorUserId);
        authorNovelService.deleteChapter(id, authorUserId);
        return Result.success();
    }
}
