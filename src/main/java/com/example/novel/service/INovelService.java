package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminNovelCreateDTO;
import com.example.novel.dto.AdminNovelQueryDTO;
import com.example.novel.dto.AdminNovelUpdateDTO;
import com.example.novel.dto.NovelQueryDTO;
import com.example.novel.vo.NovelVO;

/**
 * 小说服务接口
 */
public interface INovelService {

    /**
     * 分页查询小说列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    Page<NovelVO> listByPage(NovelQueryDTO queryDTO);

    /**
     * 获取小说详情
     *
     * @param id 小说ID
     * @return 小说详情
     */
    NovelVO getById(Long id);

    /**
     * 获取热门小说推荐
     *
     * @param limit 推荐数量
     * @return 热门小说列表
     */
    Page<NovelVO> listHotNovels(int limit);

    /**
     * 管理员分页查询小说列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    Page<NovelVO> listAdminByPage(AdminNovelQueryDTO queryDTO);

    /**
     * 管理员获取小说详情（包含下架小说）
     *
     * @param id 小说ID
     * @return 小说详情
     */
    NovelVO getAdminById(Long id);

    /**
     * 创建小说
     *
     * @param createDTO 创建参数
     * @return 小说ID
     */
    Long createNovel(AdminNovelCreateDTO createDTO);

    /**
     * 更新小说
     *
     * @param id 小说ID
     * @param updateDTO 更新参数
     */
    void updateNovel(Long id, AdminNovelUpdateDTO updateDTO);

    /**
     * 删除小说（同时删除其所有章节）
     *
     * @param id 小说ID
     */
    void deleteNovel(Long id);
}