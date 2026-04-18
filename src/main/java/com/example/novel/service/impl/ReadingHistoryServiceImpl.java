package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.entity.ReadingHistory;
import com.example.novel.mapper.ChapterMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.mapper.ReadingHistoryMapper;
import com.example.novel.service.IReadingHistoryService;
import com.example.novel.vo.ReadingHistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 阅读记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingHistoryServiceImpl implements IReadingHistoryService {

    private final ReadingHistoryMapper readingHistoryMapper;
    private final NovelMapper novelMapper;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(ReadingHistory readingHistory) {
        log.info("保存或更新阅读记录, userId: {}, novelId: {}, chapterId: {}",
                readingHistory.getUserId(), readingHistory.getNovelId(), readingHistory.getChapterId());

        // 检查是否已存在记录
        LambdaQueryWrapper<ReadingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingHistory::getUserId, readingHistory.getUserId())
                   .eq(ReadingHistory::getNovelId, readingHistory.getNovelId());

        ReadingHistory existing = readingHistoryMapper.selectOne(queryWrapper);

        if (existing != null) {
            // 更新现有记录
            existing.setChapterId(readingHistory.getChapterId());
            existing.setProgress(readingHistory.getProgress());
            existing.setLastReadTime(LocalDateTime.now());
            readingHistoryMapper.updateById(existing);
            log.info("更新阅读记录, id: {}", existing.getId());
            return existing.getId();
        } else {
            // 插入新记录
            readingHistory.setLastReadTime(LocalDateTime.now());
            readingHistory.setCreateTime(LocalDateTime.now());
            readingHistoryMapper.insert(readingHistory);
            log.info("新增阅读记录, id: {}", readingHistory.getId());
            return readingHistory.getId();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadingHistoryVO> listByUserId(Long userId, Integer pageNum, Integer pageSize) {
        log.info("分页查询用户阅读记录, userId: {}, pageNum: {}, pageSize: {}", userId, pageNum, pageSize);

        if (userId == null) {
            log.warn("用户ID不能为空");
            return new Page<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<ReadingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingHistory::getUserId, userId)
                   .orderByDesc(ReadingHistory::getLastReadTime);

        // 执行分页查询
        Page<ReadingHistory> page = new Page<>(pageNum, pageSize);
        Page<ReadingHistory> historyPage = readingHistoryMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条阅读记录，共 {} 页", historyPage.getTotal(), historyPage.getPages());

        // 转换为VO
        return convertToVOPage(historyPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadingHistoryVO getByUserAndNovel(Long userId, Long novelId) {
        log.info("查询用户小说阅读记录, userId: {}, novelId: {}", userId, novelId);

        LambdaQueryWrapper<ReadingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReadingHistory::getUserId, userId)
                   .eq(ReadingHistory::getNovelId, novelId);

        ReadingHistory history = readingHistoryMapper.selectOne(queryWrapper);
        if (history == null) {
            return null;
        }

        return convertToVO(history);
    }

    /**
     * 将 ReadingHistory 实体转换为 ReadingHistoryVO
     */
    private ReadingHistoryVO convertToVO(ReadingHistory history) {
        ReadingHistoryVO.ReadingHistoryVOBuilder builder = ReadingHistoryVO.builder()
                .id(history.getId())
                .userId(history.getUserId())
                .novelId(history.getNovelId())
                .chapterId(history.getChapterId())
                .progress(history.getProgress())
                .lastReadTime(history.getLastReadTime())
                .createTime(history.getCreateTime());

        // 查询小说信息
        try {
            com.example.novel.entity.Novel novel = novelMapper.selectById(history.getNovelId());
            if (novel != null) {
                builder.novelTitle(novel.getTitle());
                builder.novelCoverImage(novel.getCoverImage());
            }
        } catch (Exception e) {
            log.warn("查询小说信息失败, novelId: {}", history.getNovelId(), e);
        }

        // 查询章节信息
        try {
            com.example.novel.entity.Chapter chapter = chapterMapper.selectById(history.getChapterId());
            if (chapter != null) {
                builder.chapterTitle(chapter.getTitle());
                builder.chapterNumber(chapter.getChapterNumber());
            }
        } catch (Exception e) {
            log.warn("查询章节信息失败, chapterId: {}", history.getChapterId(), e);
        }

        return builder.build();
    }

    /**
     * 将 ReadingHistory 分页转换为 ReadingHistoryVO 分页
     */
    private Page<ReadingHistoryVO> convertToVOPage(Page<ReadingHistory> historyPage) {
        Page<ReadingHistoryVO> voPage = new Page<>();
        voPage.setCurrent(historyPage.getCurrent());
        voPage.setSize(historyPage.getSize());
        voPage.setTotal(historyPage.getTotal());
        voPage.setPages(historyPage.getPages());

        List<ReadingHistoryVO> records = historyPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }
}