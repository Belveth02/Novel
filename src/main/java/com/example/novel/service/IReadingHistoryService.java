package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.entity.ReadingHistory;
import com.example.novel.vo.ReadingHistoryVO;

/**
 * 阅读记录服务接口
 */
public interface IReadingHistoryService {

    /**
     * 保存或更新阅读记录
     *
     * @param readingHistory 阅读记录实体
     * @return 保存后的记录ID
     */
    Long saveOrUpdate(ReadingHistory readingHistory);

    /**
     * 分页查询用户的阅读记录
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ReadingHistoryVO> listByUserId(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询用户对某本小说的阅读记录
     *
     * @param userId 用户ID
     * @param novelId 小说ID
     * @return 阅读记录，不存在则返回null
     */
    ReadingHistoryVO getByUserAndNovel(Long userId, Long novelId);
}