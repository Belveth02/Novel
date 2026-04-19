package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.dto.AdminChapterCreateDTO;
import com.example.novel.dto.AdminChapterQueryDTO;
import com.example.novel.dto.AdminChapterUpdateDTO;
import com.example.novel.entity.Chapter;
import com.example.novel.entity.Novel;
import com.example.novel.mapper.ChapterMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.service.IChapterService;
import com.example.novel.vo.ChapterDetailVO;
import com.example.novel.vo.ChapterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final NovelMapper novelMapper;

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

    @Override
    @Transactional(readOnly = true)
    public ChapterDetailVO getChapterById(Long chapterId) {
        log.info("查询章节详情, chapterId: {}", chapterId);

        if (chapterId == null) {
            log.warn("章节ID不能为空");
            return null;
        }

        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            log.warn("章节不存在, chapterId: {}", chapterId);
            return null;
        }

        // 查询上一章和下一章ID
        Long prevChapterId = getPrevChapterId(chapter.getNovelId(), chapter.getChapterNumber());
        Long nextChapterId = getNextChapterId(chapter.getNovelId(), chapter.getChapterNumber());

        // 查询小说标题
        String novelTitle = "";
        try {
            Novel novel = novelMapper.selectById(chapter.getNovelId());
            if (novel != null) {
                novelTitle = novel.getTitle();
            }
        } catch (Exception e) {
            log.warn("查询小说标题失败, novelId: {}", chapter.getNovelId(), e);
        }

        return ChapterDetailVO.builder()
                .id(chapter.getId())
                .novelId(chapter.getNovelId())
                .novelTitle(novelTitle)
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .chapterNumber(chapter.getChapterNumber())
                .wordCount(chapter.getWordCount())
                .prevChapterId(prevChapterId)
                .nextChapterId(nextChapterId)
                .createTime(chapter.getCreateTime())
                .build();
    }

    /**
     * 获取上一章ID
     */
    private Long getPrevChapterId(Long novelId, Integer currentChapterNumber) {
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getNovelId, novelId)
                   .lt(Chapter::getChapterNumber, currentChapterNumber)
                   .orderByDesc(Chapter::getChapterNumber)
                   .last("LIMIT 1");

        Chapter prevChapter = chapterMapper.selectOne(queryWrapper);
        return prevChapter != null ? prevChapter.getId() : null;
    }

    /**
     * 获取下一章ID
     */
    private Long getNextChapterId(Long novelId, Integer currentChapterNumber) {
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getNovelId, novelId)
                   .gt(Chapter::getChapterNumber, currentChapterNumber)
                   .orderByAsc(Chapter::getChapterNumber)
                   .last("LIMIT 1");

        Chapter nextChapter = chapterMapper.selectOne(queryWrapper);
        return nextChapter != null ? nextChapter.getId() : null;
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

    @Override
    public Page<ChapterVO> listAdminByPage(AdminChapterQueryDTO queryDTO) {
        log.info("管理员分页查询章节列表, 参数: {}", queryDTO);

        // 构建查询条件
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getNovelId, queryDTO.getNovelId())
                   .orderByAsc(Chapter::getChapterNumber);

        // 执行分页查询
        Page<Chapter> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Chapter> chapterPage = chapterMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 个章节，共 {} 页", chapterPage.getTotal(), chapterPage.getPages());
        return convertToVOPage(chapterPage);
    }

    @Override
    public ChapterDetailVO getAdminById(Long id) {
        log.info("管理员获取章节详情, ID: {}", id);

        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            log.warn("章节不存在, ID: {}", id);
            throw new BusinessException("章节不存在");
        }

        // 查询小说标题
        String novelTitle = "";
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel != null) {
            novelTitle = novel.getTitle();
        }

        return ChapterDetailVO.builder()
                .id(chapter.getId())
                .novelId(chapter.getNovelId())
                .novelTitle(novelTitle)
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .chapterNumber(chapter.getChapterNumber())
                .wordCount(chapter.getWordCount())
                .createTime(chapter.getCreateTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChapter(AdminChapterCreateDTO createDTO) {
        log.info("创建章节, 标题: {}", createDTO.getTitle());

        // 验证小说是否存在
        Novel novel = novelMapper.selectById(createDTO.getNovelId());
        if (novel == null) {
            log.warn("小说不存在, ID: {}", createDTO.getNovelId());
            throw new BusinessException("小说不存在");
        }

        // 计算章节序号（最大序号+1）
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getNovelId, createDTO.getNovelId())
                   .orderByDesc(Chapter::getChapterNumber)
                   .last("LIMIT 1");
        Chapter lastChapter = chapterMapper.selectOne(queryWrapper);
        int chapterNumber = lastChapter != null ? lastChapter.getChapterNumber() + 1 : 1;

        // 计算字数（简单按字符数计算）
        int wordCount = createDTO.getContent() != null ? createDTO.getContent().length() : 0;

        // 构建章节实体
        Chapter chapter = Chapter.builder()
                .novelId(createDTO.getNovelId())
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .chapterNumber(chapterNumber)
                .wordCount(wordCount)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 插入数据库
        int rows = chapterMapper.insert(chapter);
        if (rows != 1) {
            log.error("创建章节失败, 标题: {}", createDTO.getTitle());
            throw new BusinessException("创建章节失败");
        }

        // 更新小说的章节数和总字数
        updateNovelChapterStats(createDTO.getNovelId());

        log.info("创建章节成功, ID: {}", chapter.getId());
        return chapter.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChapter(Long id, AdminChapterUpdateDTO updateDTO) {
        log.info("更新章节, ID: {}", id);

        // 检查章节是否存在
        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            log.warn("章节不存在, ID: {}", id);
            throw new BusinessException("章节不存在");
        }

        // 更新字段（非空才更新）
        boolean updated = false;
        if (updateDTO.getTitle() != null) {
            chapter.setTitle(updateDTO.getTitle());
            updated = true;
        }
        if (updateDTO.getContent() != null) {
            chapter.setContent(updateDTO.getContent());
            // 重新计算字数
            chapter.setWordCount(updateDTO.getContent().length());
            updated = true;
        }

        if (updated) {
            chapter.setUpdateTime(LocalDateTime.now());
            int rows = chapterMapper.updateById(chapter);
            if (rows != 1) {
                log.error("更新章节失败, ID: {}", id);
                throw new BusinessException("更新章节失败");
            }

            // 更新小说的总字数
            updateNovelWordCount(chapter.getNovelId());

            log.info("更新章节成功, ID: {}", id);
        } else {
            log.debug("没有字段需要更新, ID: {}", id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChapter(Long id) {
        log.info("删除章节, ID: {}", id);

        // 检查章节是否存在
        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            log.warn("章节不存在, ID: {}", id);
            throw new BusinessException("章节不存在");
        }

        Long novelId = chapter.getNovelId();

        // 删除章节
        int rows = chapterMapper.deleteById(id);
        if (rows != 1) {
            log.error("删除章节失败, ID: {}", id);
            throw new BusinessException("删除章节失败");
        }

        // 重新计算章节序号（可选，但为了保持连续性，可以重新排序，这里先不实现）
        // 更新小说的章节数和总字数
        updateNovelChapterStats(novelId);

        log.info("删除章节成功, ID: {}", id);
    }

    /**
     * 更新小说的章节统计信息（章节数和总字数）
     */
    private void updateNovelChapterStats(Long novelId) {
        log.debug("更新小说统计信息, novelId: {}", novelId);

        // 计算章节数
        LambdaQueryWrapper<Chapter> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Chapter::getNovelId, novelId);
        int chapterCount = chapterMapper.selectCount(countWrapper).intValue();

        // 计算总字数：查询所有章节并累加
        LambdaQueryWrapper<Chapter> sumWrapper = new LambdaQueryWrapper<>();
        sumWrapper.eq(Chapter::getNovelId, novelId);
        List<Chapter> chapters = chapterMapper.selectList(sumWrapper);
        int totalWordCount = 0;
        for (Chapter chapter : chapters) {
            totalWordCount += chapter.getWordCount();
        }

        // 更新小说
        Novel novel = novelMapper.selectById(novelId);
        if (novel != null) {
            novel.setChapterCount(chapterCount);
            novel.setWordCount(totalWordCount);
            novel.setUpdateTime(LocalDateTime.now());
            novelMapper.updateById(novel);
            log.debug("小说统计信息更新完成, 章节数: {}, 总字数: {}", chapterCount, totalWordCount);
        }
    }

    /**
     * 更新小说的总字数
     */
    private void updateNovelWordCount(Long novelId) {
        log.debug("更新小说总字数, novelId: {}", novelId);

        // 计算总字数
        LambdaQueryWrapper<Chapter> sumWrapper = new LambdaQueryWrapper<>();
        sumWrapper.eq(Chapter::getNovelId, novelId);
        List<Chapter> chapters = chapterMapper.selectList(sumWrapper);
        int totalWordCount = 0;
        for (Chapter chapter : chapters) {
            totalWordCount += chapter.getWordCount();
        }

        // 更新小说
        Novel novel = novelMapper.selectById(novelId);
        if (novel != null) {
            novel.setWordCount(totalWordCount);
            novel.setUpdateTime(LocalDateTime.now());
            novelMapper.updateById(novel);
            log.debug("小说总字数更新完成, 总字数: {}", totalWordCount);
        }
    }
}