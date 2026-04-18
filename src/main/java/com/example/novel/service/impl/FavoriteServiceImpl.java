package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.entity.Favorite;
import com.example.novel.mapper.FavoriteMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.service.IFavoriteService;
import com.example.novel.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements IFavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final NovelMapper novelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long userId, Long novelId) {
        log.info("收藏/取消收藏操作, userId: {}, novelId: {}", userId, novelId);

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getNovelId, novelId);

        Favorite existing = favoriteMapper.selectOne(queryWrapper);

        if (existing != null) {
            // 已收藏，执行取消收藏
            favoriteMapper.deleteById(existing.getId());
            log.info("取消收藏成功, favoriteId: {}", existing.getId());
            return false;
        } else {
            // 未收藏，执行收藏
            Favorite favorite = Favorite.builder()
                    .userId(userId)
                    .novelId(novelId)
                    .createTime(LocalDateTime.now())
                    .build();
            favoriteMapper.insert(favorite);
            log.info("收藏成功, favoriteId: {}", favorite.getId());
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long novelId) {
        log.info("检查收藏状态, userId: {}, novelId: {}", userId, novelId);

        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getNovelId, novelId);

        Favorite favorite = favoriteMapper.selectOne(queryWrapper);
        boolean isFavorited = favorite != null;
        log.info("收藏状态: {}", isFavorited ? "已收藏" : "未收藏");

        return isFavorited;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteVO> listByUserId(Long userId, Integer pageNum, Integer pageSize) {
        log.info("分页查询用户收藏列表, userId: {}, pageNum: {}, pageSize: {}", userId, pageNum, pageSize);

        if (userId == null) {
            log.warn("用户ID不能为空");
            return new Page<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .orderByDesc(Favorite::getCreateTime);

        // 执行分页查询
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        Page<Favorite> favoritePage = favoriteMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条收藏记录，共 {} 页", favoritePage.getTotal(), favoritePage.getPages());

        // 转换为VO
        return convertToVOPage(favoritePage);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByUserId(Long userId) {
        log.info("统计用户收藏数量, userId: {}", userId);

        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId);

        Long count = favoriteMapper.selectCount(queryWrapper);
        log.info("用户收藏数量: {}", count);

        return count;
    }

    /**
     * 将 Favorite 实体转换为 FavoriteVO
     */
    private FavoriteVO convertToVO(Favorite favorite) {
        FavoriteVO.FavoriteVOBuilder builder = FavoriteVO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .novelId(favorite.getNovelId())
                .createTime(favorite.getCreateTime());

        // 查询小说信息
        try {
            com.example.novel.entity.Novel novel = novelMapper.selectById(favorite.getNovelId());
            if (novel != null) {
                builder.novelTitle(novel.getTitle());
                builder.novelCoverImage(novel.getCoverImage());
                builder.novelAuthor(novel.getAuthor());
                builder.novelDescription(novel.getDescription());
                builder.novelWordCount(novel.getWordCount());
                builder.novelChapterCount(novel.getChapterCount());
                builder.novelViewCount(novel.getViewCount());
            }
        } catch (Exception e) {
            log.warn("查询小说信息失败, novelId: {}", favorite.getNovelId(), e);
        }

        return builder.build();
    }

    /**
     * 将 Favorite 分页转换为 FavoriteVO 分页
     */
    private Page<FavoriteVO> convertToVOPage(Page<Favorite> favoritePage) {
        Page<FavoriteVO> voPage = new Page<>();
        voPage.setCurrent(favoritePage.getCurrent());
        voPage.setSize(favoritePage.getSize());
        voPage.setTotal(favoritePage.getTotal());
        voPage.setPages(favoritePage.getPages());

        List<FavoriteVO> records = favoritePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }
}