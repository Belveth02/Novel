package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminChapterCreateDTO;
import com.example.novel.dto.AdminChapterQueryDTO;
import com.example.novel.dto.AdminChapterUpdateDTO;
import com.example.novel.vo.ChapterDetailVO;
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

    /**
     * 根据ID查询章节详情
     *
     * @param chapterId 章节ID
     * @return 章节详情
     */
    ChapterDetailVO getChapterById(Long chapterId);

    /**
     * 管理员分页查询章节列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    Page<ChapterVO> listAdminByPage(AdminChapterQueryDTO queryDTO);

    /**
     * 管理员获取章节详情
     *
     * @param id 章节ID
     * @return 章节详情
     */
    ChapterDetailVO getAdminById(Long id);

    /**
     * 创建章节
     *
     * @param createDTO 创建参数
     * @return 章节ID
     */
    Long createChapter(AdminChapterCreateDTO createDTO);

    /**
     * 更新章节
     *
     * @param id 章节ID
     * @param updateDTO 更新参数
     */
    void updateChapter(Long id, AdminChapterUpdateDTO updateDTO);

    /**
     * 删除章节
     *
     * @param id 章节ID
     */
    void deleteChapter(Long id);
}