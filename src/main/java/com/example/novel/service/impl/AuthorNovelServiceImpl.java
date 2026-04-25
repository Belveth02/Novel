package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AuthorChapterCreateDTO;
import com.example.novel.dto.AuthorChapterQueryDTO;
import com.example.novel.dto.AuthorChapterUpdateDTO;
import com.example.novel.dto.AuthorNovelCreateDTO;
import com.example.novel.dto.AuthorNovelQueryDTO;
import com.example.novel.dto.AuthorNovelUpdateDTO;
import com.example.novel.entity.Chapter;
import com.example.novel.entity.Comment;
import com.example.novel.entity.Novel;
import com.example.novel.entity.User;
import com.example.novel.mapper.ChapterMapper;
import com.example.novel.mapper.CommentMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.mapper.UserMapper;
import com.example.novel.service.IAuthorNovelService;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.vo.AuthorNovelStatsVO;
import com.example.novel.vo.ChapterDetailVO;
import com.example.novel.vo.ChapterVO;
import com.example.novel.vo.CommentVO;
import com.example.novel.vo.NovelVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者小说服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorNovelServiceImpl implements IAuthorNovelService {

    private final NovelMapper novelMapper;
    private final ChapterMapper chapterMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public Page<NovelVO> listByAuthor(Long authorUserId, AuthorNovelQueryDTO queryDTO) {
        log.info("作者查询自己的小说列表, 作者ID: {}, 参数: {}", authorUserId, queryDTO);

        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Novel::getAuthorUserId, authorUserId);

        // 关键词筛选（标题）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            queryWrapper.like(Novel::getTitle, queryDTO.getKeyword().trim());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Novel::getStatus, queryDTO.getStatus());
        }

        // 排序处理
        String sortField = queryDTO.getSortField();
        String sortOrder = queryDTO.getSortOrder();
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);

        if ("view_count".equals(sortField)) {
            queryWrapper.orderBy(true, isAsc, Novel::getViewCount);
        } else if ("update_time".equals(sortField)) {
            queryWrapper.orderBy(true, isAsc, Novel::getUpdateTime);
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(Novel::getCreateTime);
        }

        Page<Novel> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Novel> novelPage = novelMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条小说记录", novelPage.getTotal());
        return convertToVOPage(novelPage);
    }

    @Override
    public NovelVO getById(Long novelId, Long authorUserId) {
        log.info("作者获取小说详情, 小说ID: {}, 作者ID: {}", novelId, authorUserId);

        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }

        // 验证是否是该作者的小说
        if (!novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权访问该小说");
        }

        return convertToVO(novel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNovel(Long authorUserId, AuthorNovelCreateDTO createDTO) {
        log.info("作者创建小说, 作者ID: {}, 标题: {}", authorUserId, createDTO.getTitle());

        // 构建小说实体
        Novel novel = Novel.builder()
                .title(createDTO.getTitle())
                .author(createDTO.getAuthor())
                .authorUserId(authorUserId)
                .coverImage(createDTO.getCoverImage())
                .description(createDTO.getDescription())
                .categoryId(createDTO.getCategoryId())
                .status(1) // 默认上架
                .wordCount(0)
                .chapterCount(0)
                .viewCount(0)
                .isHot(0)
                .hotSort(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        int rows = novelMapper.insert(novel);
        if (rows != 1) {
            throw new BusinessException("创建小说失败");
        }

        log.info("作者创建小说成功, ID: {}", novel.getId());
        return novel.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNovel(Long novelId, Long authorUserId, AuthorNovelUpdateDTO updateDTO) {
        log.info("作者更新小说, 小说ID: {}, 作者ID: {}", novelId, authorUserId);

        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }

        // 验证是否是该作者的小说
        if (!novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权修改该小说");
        }

        // 更新字段
        if (updateDTO.getTitle() != null) {
            novel.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAuthor() != null) {
            novel.setAuthor(updateDTO.getAuthor());
        }
        if (updateDTO.getCoverImage() != null) {
            novel.setCoverImage(updateDTO.getCoverImage());
        }
        if (updateDTO.getDescription() != null) {
            novel.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getCategoryId() != null) {
            novel.setCategoryId(updateDTO.getCategoryId());
        }
        if (updateDTO.getStatus() != null) {
            novel.setStatus(updateDTO.getStatus());
        }

        novel.setUpdateTime(LocalDateTime.now());
        novelMapper.updateById(novel);

        log.info("作者更新小说成功, ID: {}", novelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNovel(Long novelId, Long authorUserId) {
        log.info("作者删除小说, 小说ID: {}, 作者ID: {}", novelId, authorUserId);

        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }

        // 验证是否是该作者的小说
        if (!novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权删除该小说");
        }

        // 删除该小说的所有章节
        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(Chapter::getNovelId, novelId);
        chapterMapper.delete(chapterWrapper);

        // 删除该小说的所有评论
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getNovelId, novelId);
        commentMapper.delete(commentWrapper);

        // 删除小说
        novelMapper.deleteById(novelId);

        log.info("作者删除小说成功, ID: {}", novelId);
    }

    @Override
    public Page<ChapterVO> listChapters(Long novelId, Long authorUserId, AuthorChapterQueryDTO queryDTO) {
        log.info("作者查询章节列表, 小说ID: {}, 作者ID: {}", novelId, authorUserId);

        // 如果没有指定小说ID，返回空列表
        if (novelId == null) {
            return new Page<>(queryDTO.getPage(), queryDTO.getSize(), 0);
        }

        // 验证小说归属
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }
        if (!novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权访问该小说");
        }

        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getNovelId, novelId);

        // 排序
        boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
        queryWrapper.orderBy(true, isAsc, Chapter::getChapterNumber);

        Page<Chapter> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Chapter> chapterPage = chapterMapper.selectPage(page, queryWrapper);

        return convertChapterToVOPage(chapterPage);
    }

    @Override
    public ChapterDetailVO getChapterById(Long chapterId, Long authorUserId) {
        log.info("作者获取章节详情, 章节ID: {}, 作者ID: {}", chapterId, authorUserId);

        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        // 验证小说归属
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel == null || !novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权访问该章节");
        }

        return convertChapterToDetailVO(chapter, novel.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChapter(Long authorUserId, AuthorChapterCreateDTO createDTO) {
        log.info("作者创建章节, 作者ID: {}, 小说ID: {}, 章节号: {}", authorUserId, createDTO.getNovelId(), createDTO.getChapterNumber());

        // 验证小说归属
        Novel novel = novelMapper.selectById(createDTO.getNovelId());
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }
        if (!novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权在该小说中创建章节");
        }

        // 检查章节序号是否已存在
        LambdaQueryWrapper<Chapter> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Chapter::getNovelId, createDTO.getNovelId())
                .eq(Chapter::getChapterNumber, createDTO.getChapterNumber());
        if (chapterMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessException("该章节序号已存在");
        }

        // 计算字数
        int wordCount = calculateWordCount(createDTO.getContent());

        Chapter chapter = Chapter.builder()
                .novelId(createDTO.getNovelId())
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .chapterNumber(createDTO.getChapterNumber())
                .wordCount(wordCount)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        chapterMapper.insert(chapter);

        // 更新小说的章节数和字数
        updateNovelStats(createDTO.getNovelId());

        log.info("作者创建章节成功, ID: {}", chapter.getId());
        return chapter.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChapter(Long chapterId, Long authorUserId, AuthorChapterUpdateDTO updateDTO) {
        log.info("作者更新章节, 章节ID: {}, 作者ID: {}", chapterId, authorUserId);

        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        // 验证小说归属
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel == null || !novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权修改该章节");
        }

        // 如果修改了章节号，检查是否冲突
        if (updateDTO.getChapterNumber() != null && !updateDTO.getChapterNumber().equals(chapter.getChapterNumber())) {
            LambdaQueryWrapper<Chapter> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Chapter::getNovelId, chapter.getNovelId())
                    .eq(Chapter::getChapterNumber, updateDTO.getChapterNumber());
            if (chapterMapper.selectCount(checkWrapper) > 0) {
                throw new BusinessException("该章节序号已存在");
            }
            chapter.setChapterNumber(updateDTO.getChapterNumber());
        }

        if (updateDTO.getTitle() != null) {
            chapter.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getContent() != null) {
            chapter.setContent(updateDTO.getContent());
            chapter.setWordCount(calculateWordCount(updateDTO.getContent()));
        }

        chapter.setUpdateTime(LocalDateTime.now());
        chapterMapper.updateById(chapter);

        // 更新小说的字数统计
        updateNovelStats(chapter.getNovelId());

        log.info("作者更新章节成功, ID: {}", chapterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChapter(Long chapterId, Long authorUserId) {
        log.info("作者删除章节, 章节ID: {}, 作者ID: {}", chapterId, authorUserId);

        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        Long novelId = chapter.getNovelId();

        // 验证小说归属
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null || !novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权删除该章节");
        }

        chapterMapper.deleteById(chapterId);

        // 更新小说的章节数和字数
        updateNovelStats(novelId);

        log.info("作者删除章节成功, ID: {}", chapterId);
    }

    @Override
    public Page<CommentVO> listComments(Long novelId, Long authorUserId, Integer page, Integer size) {
        log.info("作者查询评论列表, 小说ID: {}, 作者ID: {}", novelId, authorUserId);

        // 如果没有指定小说ID，返回空列表
        if (novelId == null) {
            return new Page<>(page, size, 0);
        }

        // 验证小说归属
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }
        if (!novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权查看该小说的评论");
        }

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getNovelId, novelId);
        queryWrapper.orderByDesc(Comment::getCreateTime);

        Page<Comment> commentPage = new Page<>(page, size);
        commentPage = commentMapper.selectPage(commentPage, queryWrapper);

        return convertCommentToVOPage(commentPage, novel.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long novelId, Long authorUserId) {
        log.info("作者删除评论, 评论ID: {}, 小说ID: {}, 作者ID: {}", commentId, novelId, authorUserId);

        // 验证小说归属
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null || !novel.getAuthorUserId().equals(authorUserId)) {
            throw new BusinessException("无权操作");
        }

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (!comment.getNovelId().equals(novelId)) {
            throw new BusinessException("评论不属于该小说");
        }

        commentMapper.deleteById(commentId);
        log.info("作者删除评论成功, ID: {}", commentId);
    }

    @Override
    public AuthorNovelStatsVO getStats(Long authorUserId) {
        log.info("获取作者统计数据, 作者ID: {}", authorUserId);

        // 查询作者的所有小说
        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Novel::getAuthorUserId, authorUserId);
        List<Novel> novels = novelMapper.selectList(queryWrapper);

        int totalNovels = novels.size();
        int publishedNovels = (int) novels.stream().filter(n -> n.getStatus() == 1).count();
        int unpublishedNovels = totalNovels - publishedNovels;
        int totalChapters = novels.stream().mapToInt(Novel::getChapterCount).sum();
        int totalWordCount = novels.stream().mapToInt(Novel::getWordCount).sum();
        int totalViewCount = novels.stream().mapToInt(Novel::getViewCount).sum();

        // 统计评论数
        List<Long> novelIds = novels.stream().map(Novel::getId).collect(Collectors.toList());
        long totalComments = 0;
        if (!novelIds.isEmpty()) {
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.in(Comment::getNovelId, novelIds);
            totalComments = commentMapper.selectCount(commentWrapper);
        }

        return AuthorNovelStatsVO.builder()
                .totalNovels(totalNovels)
                .totalChapters(totalChapters)
                .totalWordCount(totalWordCount)
                .totalViewCount(totalViewCount)
                .totalComments((int) totalComments)
                .publishedNovels(publishedNovels)
                .unpublishedNovels(unpublishedNovels)
                .build();
    }

    @Override
    public List<Long> getNovelIdsByAuthor(Long authorUserId) {
        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Novel::getAuthorUserId, authorUserId);
        queryWrapper.select(Novel::getId);
        return novelMapper.selectList(queryWrapper).stream()
                .map(Novel::getId)
                .collect(Collectors.toList());
    }

    /**
     * 更新小说的章节数和字数统计
     */
    private void updateNovelStats(Long novelId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getNovelId, novelId);
        List<Chapter> chapters = chapterMapper.selectList(wrapper);

        int chapterCount = chapters.size();
        int wordCount = chapters.stream().mapToInt(Chapter::getWordCount).sum();

        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setChapterCount(chapterCount);
        novel.setWordCount(wordCount);
        novel.setUpdateTime(LocalDateTime.now());

        novelMapper.updateById(novel);
    }

    /**
     * 计算字数（简单实现，计算字符数）
     */
    private int calculateWordCount(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        // 移除空白字符后计算长度
        return content.replaceAll("\\s+", "").length();
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
                .isHot(novel.getIsHot())
                .hotSort(novel.getHotSort())
                .createTime(novel.getCreateTime())
                .build();
    }

    /**
     * 将 Novel 分页转换为 NovelVO 分页
     */
    private Page<NovelVO> convertToVOPage(Page<Novel> novelPage) {
        Page<NovelVO> voPage = new Page<>(novelPage.getCurrent(), novelPage.getSize(), novelPage.getTotal());

        List<NovelVO> records = novelPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 将 Chapter 实体转换为 ChapterVO
     */
    private ChapterVO convertChapterToVO(Chapter chapter) {
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
    private Page<ChapterVO> convertChapterToVOPage(Page<Chapter> chapterPage) {
        Page<ChapterVO> voPage = new Page<>(chapterPage.getCurrent(), chapterPage.getSize(), chapterPage.getTotal());

        List<ChapterVO> records = chapterPage.getRecords().stream()
                .map(this::convertChapterToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 将 Chapter 实体转换为 ChapterDetailVO
     */
    private ChapterDetailVO convertChapterToDetailVO(Chapter chapter, String novelTitle) {
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

    /**
     * 将 Comment 分页转换为 CommentVO 分页
     */
    private Page<CommentVO> convertCommentToVOPage(Page<Comment> commentPage, String novelTitle) {
        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());

        List<CommentVO> records = commentPage.getRecords().stream()
                .map(comment -> convertCommentToVO(comment, novelTitle))
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 将 Comment 实体转换为 CommentVO
     */
    private CommentVO convertCommentToVO(Comment comment, String novelTitle) {
        // 查询用户信息
        User user = userMapper.selectById(comment.getUserId());

        return CommentVO.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .username(user != null ? user.getUsername() : "未知用户")
                .nickname(user != null ? user.getNickname() : "未知用户")
                .avatar(user != null ? user.getAvatar() : null)
                .novelId(comment.getNovelId())
                .novelTitle(novelTitle)
                .content(comment.getContent())
                .createTime(comment.getCreateTime())
                .build();
    }
}
