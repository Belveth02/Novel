package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.vo.CommentVO;

/**
 * 评论服务接口
 */
public interface ICommentService {

    /**
     * 发布评论
     *
     * @param userId 用户ID
     * @param novelId 小说ID
     * @param content 评论内容
     * @return 评论ID
     */
    Long addComment(Long userId, Long novelId, String content);

    /**
     * 分页查询小说的评论列表
     *
     * @param novelId 小说ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<CommentVO> listByNovelId(Long novelId, Integer pageNum, Integer pageSize);

    /**
     * 删除评论（仅限评论发布者）
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return true-删除成功，false-删除失败
     */
    boolean deleteComment(Long userId, Long commentId);

    /**
     * 统计小说的评论数量
     *
     * @param novelId 小说ID
     * @return 评论数量
     */
    Long countByNovelId(Long novelId);
}