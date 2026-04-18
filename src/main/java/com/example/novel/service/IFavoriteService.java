package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.vo.FavoriteVO;

/**
 * 收藏服务接口
 */
public interface IFavoriteService {

    /**
     * 收藏或取消收藏
     *
     * @param userId 用户ID
     * @param novelId 小说ID
     * @return true-收藏成功，false-取消收藏
     */
    boolean toggleFavorite(Long userId, Long novelId);

    /**
     * 检查用户是否已收藏某小说
     *
     * @param userId 用户ID
     * @param novelId 小说ID
     * @return true-已收藏，false-未收藏
     */
    boolean isFavorite(Long userId, Long novelId);

    /**
     * 分页查询用户的收藏列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<FavoriteVO> listByUserId(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 统计用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    Long countByUserId(Long userId);
}