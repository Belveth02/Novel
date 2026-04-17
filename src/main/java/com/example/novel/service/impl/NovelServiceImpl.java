package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.NovelQueryDTO;
import com.example.novel.entity.Novel;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.service.ICategoryService;
import com.example.novel.service.INovelService;
import com.example.novel.vo.NovelVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小说服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovelServiceImpl implements INovelService {

    private final NovelMapper novelMapper;
    private final ICategoryService categoryService;

    @Override
    public Page<NovelVO> listByPage(NovelQueryDTO queryDTO) {
        log.info("分页查询小说列表, 参数: {}", queryDTO);

        // 验证分类是否存在（如果提供了分类ID）
        if (queryDTO.getCategoryId() != null) {
            // 通过分类服务验证分类是否存在
            // 这里简单检查，实际可以调用分类服务获取分类信息
            log.debug("验证分类ID: {}", queryDTO.getCategoryId());
            // 注意：这里需要实际验证，暂时跳过，假设分类存在
        }

        // 构建查询条件
        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Novel::getCategoryId, queryDTO.getCategoryId());
        }

        // 状态筛选：只显示上架小说（status=1）
        queryWrapper.eq(Novel::getStatus, 1);

        // 排序处理
        String sortField = queryDTO.getSortField();
        boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());

        if ("view_count".equals(sortField)) {
            queryWrapper.orderBy(true, isAsc, Novel::getViewCount);
        } else if ("create_time".equals(sortField)) {
            queryWrapper.orderBy(true, isAsc, Novel::getCreateTime);
        } else {
            // 默认按阅读量降序
            queryWrapper.orderByDesc(Novel::getViewCount);
        }

        // 执行分页查询
        Page<Novel> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Novel> novelPage = novelMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条记录，共 {} 页", novelPage.getTotal(), novelPage.getPages());

        // 转换为VO
        return convertToVOPage(novelPage);
    }

    @Override
    public NovelVO getById(Long id) {
        log.info("获取小说详情, ID: {}", id);

        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            log.warn("小说不存在, ID: {}", id);
            return null;
        }

        // 只返回上架小说
        if (novel.getStatus() != 1) {
            log.warn("小说已下架, ID: {}", id);
            return null;
        }

        log.debug("找到小说: {}", novel.getTitle());
        return convertToVO(novel);
    }

    @Override
    public Page<NovelVO> listHotNovels(int limit) {
        log.info("获取热门小说推荐, 数量: {}", limit);

        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Novel::getStatus, 1)
                   .orderByDesc(Novel::getViewCount);

        Page<Novel> page = new Page<>(1, limit);
        Page<Novel> novelPage = novelMapper.selectPage(page, queryWrapper);

        log.info("推荐 {} 本热门小说", novelPage.getRecords().size());
        return convertToVOPage(novelPage);
    }

    /**
     * 将 Novel 实体转换为 NovelVO
     */
    private NovelVO convertToVO(Novel novel) {
        return NovelVO.builder()
                .id(novel.getId())
                .title(novel.getTitle())
                .author(novel.getAuthor())
                .coverImage(novel.getCoverImage())
                .description(novel.getDescription())
                .categoryId(novel.getCategoryId())
                .status(novel.getStatus())
                .wordCount(novel.getWordCount())
                .chapterCount(novel.getChapterCount())
                .viewCount(novel.getViewCount())
                .createTime(novel.getCreateTime())
                .build();
    }

    /**
     * 将 Novel 分页转换为 NovelVO 分页
     */
    private Page<NovelVO> convertToVOPage(Page<Novel> novelPage) {
        Page<NovelVO> voPage = new Page<>();
        voPage.setCurrent(novelPage.getCurrent());
        voPage.setSize(novelPage.getSize());
        voPage.setTotal(novelPage.getTotal());
        voPage.setPages(novelPage.getPages());

        List<NovelVO> records = novelPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }
}