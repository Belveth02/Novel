package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminNovelCreateDTO;
import com.example.novel.dto.AdminNovelQueryDTO;
import com.example.novel.dto.AdminNovelUpdateDTO;
import com.example.novel.dto.NovelQueryDTO;
import com.example.novel.entity.Chapter;
import com.example.novel.entity.Novel;
import com.example.novel.mapper.ChapterMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.service.ICategoryService;
import com.example.novel.service.INovelService;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.vo.NovelVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
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
    private final ChapterMapper chapterMapper;

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
    @Cacheable(value = "hotNovels", key = "#limit", unless = "#result == null || #result.records.isEmpty()")
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

    @Override
    public Page<NovelVO> listAdminByPage(AdminNovelQueryDTO queryDTO) {
        log.info("管理员分页查询小说列表, 参数: {}", queryDTO);

        // 构建查询条件
        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词筛选（标题/作者）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(Novel::getTitle, keyword)
                    .or()
                    .like(Novel::getAuthor, keyword)
            );
        }

        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Novel::getCategoryId, queryDTO.getCategoryId());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Novel::getStatus, queryDTO.getStatus());
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(Novel::getCreateTime);

        // 执行分页查询
        Page<Novel> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Novel> novelPage = novelMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条记录，共 {} 页", novelPage.getTotal(), novelPage.getPages());
        return convertToVOPage(novelPage);
    }

    @Override
    public NovelVO getAdminById(Long id) {
        log.info("管理员获取小说详情, ID: {}", id);

        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            log.warn("小说不存在, ID: {}", id);
            throw new BusinessException("小说不存在");
        }

        log.debug("找到小说: {}", novel.getTitle());
        return convertToVO(novel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNovel(AdminNovelCreateDTO createDTO) {
        log.info("创建小说, 标题: {}", createDTO.getTitle());

        // 验证分类是否存在
        // 这里可以调用categoryService验证分类是否存在，暂时跳过

        // 构建小说实体
        Novel novel = Novel.builder()
                .title(createDTO.getTitle())
                .author(createDTO.getAuthor())
                .coverImage(createDTO.getCoverImage())
                .description(createDTO.getDescription())
                .categoryId(createDTO.getCategoryId())
                .status(createDTO.getStatus())
                .wordCount(0) // 初始字数为0
                .chapterCount(0) // 初始章节数为0
                .viewCount(0) // 初始阅读数为0
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 插入数据库
        int rows = novelMapper.insert(novel);
        if (rows != 1) {
            log.error("创建小说失败, 标题: {}", createDTO.getTitle());
            throw new BusinessException("创建小说失败");
        }

        log.info("创建小说成功, ID: {}", novel.getId());
        return novel.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNovel(Long id, AdminNovelUpdateDTO updateDTO) {
        log.info("更新小说, ID: {}", id);

        // 检查小说是否存在
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            log.warn("小说不存在, ID: {}", id);
            throw new BusinessException("小说不存在");
        }

        // 更新字段（非空才更新）
        boolean updated = false;
        if (updateDTO.getTitle() != null) {
            novel.setTitle(updateDTO.getTitle());
            updated = true;
        }
        if (updateDTO.getAuthor() != null) {
            novel.setAuthor(updateDTO.getAuthor());
            updated = true;
        }
        if (updateDTO.getCoverImage() != null) {
            novel.setCoverImage(updateDTO.getCoverImage());
            updated = true;
        }
        if (updateDTO.getDescription() != null) {
            novel.setDescription(updateDTO.getDescription());
            updated = true;
        }
        if (updateDTO.getCategoryId() != null) {
            novel.setCategoryId(updateDTO.getCategoryId());
            updated = true;
        }
        if (updateDTO.getStatus() != null) {
            novel.setStatus(updateDTO.getStatus());
            updated = true;
        }

        if (updated) {
            novel.setUpdateTime(LocalDateTime.now());
            int rows = novelMapper.updateById(novel);
            if (rows != 1) {
                log.error("更新小说失败, ID: {}", id);
                throw new BusinessException("更新小说失败");
            }
            log.info("更新小说成功, ID: {}", id);
        } else {
            log.debug("没有字段需要更新, ID: {}", id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNovel(Long id) {
        log.info("删除小说, ID: {}", id);

        // 检查小说是否存在
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            log.warn("小说不存在, ID: {}", id);
            throw new BusinessException("小说不存在");
        }

        // 删除该小说的所有章节
        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(Chapter::getNovelId, id);
        int chapterDeleted = chapterMapper.delete(chapterWrapper);
        log.info("删除小说 {} 的 {} 个章节", id, chapterDeleted);

        // 删除小说
        int rows = novelMapper.deleteById(id);
        if (rows != 1) {
            log.error("删除小说失败, ID: {}", id);
            throw new BusinessException("删除小说失败");
        }

        log.info("删除小说成功, ID: {}", id);
    }
}