package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.vo.ChapterVO;

/**
 * 章节服务接口
 */
public interface IChapterService {

    /**
     * 分页查询章节列表
     *
     * @param novelId 小说ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ChapterVO> listByNovelId(Long novelId, Integer pageNum, Integer pageSize);
}