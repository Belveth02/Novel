package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AuthorChapterCreateDTO;
import com.example.novel.dto.AuthorChapterQueryDTO;
import com.example.novel.dto.AuthorChapterUpdateDTO;
import com.example.novel.dto.AuthorNovelCreateDTO;
import com.example.novel.dto.AuthorNovelQueryDTO;
import com.example.novel.dto.AuthorNovelUpdateDTO;
import com.example.novel.vo.AuthorNovelStatsVO;
import com.example.novel.vo.ChapterDetailVO;
import com.example.novel.vo.ChapterVO;
import com.example.novel.vo.CommentVO;
import com.example.novel.vo.NovelVO;

import java.util.List;

/**
 * 作者小说服务接口
 */
public interface IAuthorNovelService {

    /**
     * 分页查询作者自己的小说列表
     *
     * @param authorUserId 作者用户ID
     * @param queryDTO     查询参数
     * @return 分页结果
     */
    Page<NovelVO> listByAuthor(Long authorUserId, AuthorNovelQueryDTO queryDTO);

    /**
     * 获取作者的小说详情
     *
     * @param novelId      小说ID
     * @param authorUserId 作者用户ID
     * @return 小说详情
     */
    NovelVO getById(Long novelId, Long authorUserId);

    /**
     * 创建小说（自动设置作者）
     *
     * @param authorUserId 作者用户ID
     * @param createDTO    创建参数
     * @return 小说ID
     */
    Long createNovel(Long authorUserId, AuthorNovelCreateDTO createDTO);

    /**
     * 更新小说
     *
     * @param novelId      小说ID
     * @param authorUserId 作者用户ID
     * @param updateDTO    更新参数
     */
    void updateNovel(Long novelId, Long authorUserId, AuthorNovelUpdateDTO updateDTO);

    /**
     * 删除小说
     *
     * @param novelId      小说ID
     * @param authorUserId 作者用户ID
     */
    void deleteNovel(Long novelId, Long authorUserId);

    /**
     * 分页查询小说的章节列表
     *
     * @param novelId      小说ID
     * @param authorUserId 作者用户ID
     * @param queryDTO     查询参数
     * @return 分页结果
     */
    Page<ChapterVO> listChapters(Long novelId, Long authorUserId, AuthorChapterQueryDTO queryDTO);

    /**
     * 获取章节详情
     *
     * @param chapterId    章节ID
     * @param authorUserId 作者用户ID
     * @return 章节详情
     */
    ChapterDetailVO getChapterById(Long chapterId, Long authorUserId);

    /**
     * 创建章节
     *
     * @param authorUserId 作者用户ID
     * @param createDTO    创建参数
     * @return 章节ID
     */
    Long createChapter(Long authorUserId, AuthorChapterCreateDTO createDTO);

    /**
     * 更新章节
     *
     * @param chapterId    章节ID
     * @param authorUserId 作者用户ID
     * @param updateDTO    更新参数
     */
    void updateChapter(Long chapterId, Long authorUserId, AuthorChapterUpdateDTO updateDTO);

    /**
     * 删除章节
     *
     * @param chapterId    章节ID
     * @param authorUserId 作者用户ID
     */
    void deleteChapter(Long chapterId, Long authorUserId);

    /**
     * 查询小说的评论列表
     *
     * @param novelId      小说ID
     * @param authorUserId 作者用户ID
     * @param page         页码
     * @param size         每页大小
     * @return 分页结果
     */
    Page<CommentVO> listComments(Long novelId, Long authorUserId, Integer page, Integer size);

    /**
     * 删除评论
     *
     * @param commentId    评论ID
     * @param novelId      小说ID
     * @param authorUserId 作者用户ID
     */
    void deleteComment(Long commentId, Long novelId, Long authorUserId);

    /**
     * 获取作者的小说统计数据
     *
     * @param authorUserId 作者用户ID
     * @return 统计数据
     */
    AuthorNovelStatsVO getStats(Long authorUserId);

    /**
     * 获取作者的所有小说ID列表
     *
     * @param authorUserId 作者用户ID
     * @return 小说ID列表
     */
    List<Long> getNovelIdsByAuthor(Long authorUserId);
}
