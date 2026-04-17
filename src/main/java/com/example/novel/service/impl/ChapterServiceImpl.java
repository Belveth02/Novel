package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.entity.Chapter;
import com.example.novel.mapper.ChapterMapper;
import com.example.novel.service.IChapterService;
import com.example.novel.vo.ChapterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 章节服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements IChapterService {

    private final ChapterMapper chapterMapper;

    @Override
    public Page<ChapterVO> listByNovelId(Long novelId, Integer pageNum, Integer pageSize) {
        log.info("分页查询章节列表, novelId: {}, pageNum: {}, pageSize: {}", novelId, pageNum, pageSize);

        if (novelId == null) {
            log.warn("小说ID不能为空");
            return new Page<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getNovelId, novelId)
                   .orderByAsc(Chapter::getChapterNumber);

        // 执行分页查询
        Page<Chapter> page = new Page<>(pageNum, pageSize);
        Page<Chapter> chapterPage = chapterMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 个章节，共 {} 页", chapterPage.getTotal(), chapterPage.getPages());

        // 转换为VO
        return convertToVOPage(chapterPage);
    }

    /**
     * 将 Chapter 实体转换为 ChapterVO
     */
    private ChapterVO convertToVO(Chapter chapter) {
        return ChapterVO.builder()
                .id(chapter.getId())
                .novelId(chapter.getNovelId())
                .title(chapter.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .wordCount(chapter.getWordCount())
                .createTime(chapter.getCreateTime())
                .build();
    }

    /**
     * 将 Chapter 分页转换为 ChapterVO 分页
     */
    private Page<ChapterVO> convertToVOPage(Page<Chapter> chapterPage) {
        Page<ChapterVO> voPage = new Page<>();
        voPage.setCurrent(chapterPage.getCurrent());
        voPage.setSize(chapterPage.getSize());
        voPage.setTotal(chapterPage.getTotal());
        voPage.setPages(chapterPage.getPages());

        List<ChapterVO> records = chapterPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }
}