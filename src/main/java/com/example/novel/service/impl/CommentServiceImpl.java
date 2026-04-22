package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.entity.Comment;
import com.example.novel.entity.Novel;
import com.example.novel.entity.User;
import com.example.novel.mapper.CommentMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.mapper.UserMapper;
import com.example.novel.service.ICommentService;
import com.example.novel.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final NovelMapper novelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(Long userId, Long novelId, String content) {
        log.info("发布评论, userId: {}, novelId: {}, content: {}", userId, novelId, content);

        // 参数校验
        if (userId == null || novelId == null || content == null || content.trim().isEmpty()) {
            log.warn("评论参数不完整, userId: {}, novelId: {}, content: {}", userId, novelId, content);
            throw new IllegalArgumentException("评论参数不完整");
        }

        // 创建评论实体
        Comment comment = Comment.builder()
                .userId(userId)
                .novelId(novelId)
                .content(content.trim())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 保存评论
        int result = commentMapper.insert(comment);
        if (result > 0) {
            log.info("评论发布成功, commentId: {}", comment.getId());
            return comment.getId();
        } else {
            log.error("评论发布失败, userId: {}, novelId: {}", userId, novelId);
            throw new RuntimeException("评论发布失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentVO> listByNovelId(Long novelId, Integer pageNum, Integer pageSize) {
        log.info("分页查询小说评论列表, novelId: {}, pageNum: {}, pageSize: {}", novelId, pageNum, pageSize);

        if (novelId == null) {
            log.warn("小说ID不能为空");
            return new Page<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getNovelId, novelId)
                   .orderByDesc(Comment::getCreateTime);

        // 执行分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条评论，共 {} 页", commentPage.getTotal(), commentPage.getPages());

        // 转换为VO
        return convertToVOPage(commentPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long userId, Long commentId) {
        log.info("删除评论请求, userId: {}, commentId: {}", userId, commentId);

        if (userId == null || commentId == null) {
            log.warn("删除评论参数不完整, userId: {}, commentId: {}", userId, commentId);
            return false;
        }

        // 查询评论是否存在且属于该用户
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            log.warn("评论不存在, commentId: {}", commentId);
            return false;
        }

        if (!comment.getUserId().equals(userId)) {
            log.warn("用户无权删除此评论, userId: {}, commentUserId: {}", userId, comment.getUserId());
            return false;
        }

        // 删除评论
        int result = commentMapper.deleteById(commentId);
        boolean success = result > 0;
        log.info("评论删除{}, commentId: {}, userId: {}", success ? "成功" : "失败", commentId, userId);

        return success;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByNovelId(Long novelId) {
        log.info("统计小说评论数量, novelId: {}", novelId);

        if (novelId == null) {
            log.warn("小说ID不能为空");
            return 0L;
        }

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getNovelId, novelId);

        Long count = commentMapper.selectCount(queryWrapper);
        log.info("小说评论数量: {}, novelId: {}", count, novelId);

        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentVO> listByUserId(Long userId, Integer pageNum, Integer pageSize) {
        log.info("分页查询用户评论列表, userId: {}, pageNum: {}, pageSize: {}", userId, pageNum, pageSize);

        if (userId == null) {
            log.warn("用户ID不能为空");
            return new Page<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getUserId, userId)
                   .orderByDesc(Comment::getCreateTime);

        // 执行分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条评论，共 {} 页", commentPage.getTotal(), commentPage.getPages());

        // 转换为VO
        return convertToVOPageWithNovelTitle(commentPage);
    }

    /**
     * 将 Comment 分页转换为 CommentVO 分页（包含小说标题）
     */
    private Page<CommentVO> convertToVOPageWithNovelTitle(Page<Comment> commentPage) {
        Page<CommentVO> voPage = new Page<>();
        voPage.setCurrent(commentPage.getCurrent());
        voPage.setSize(commentPage.getSize());
        voPage.setTotal(commentPage.getTotal());
        voPage.setPages(commentPage.getPages());

        List<CommentVO> records = commentPage.getRecords().stream()
                .map(this::convertToVOWithNovelTitle)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 将 Comment 实体转换为 CommentVO（包含小说标题）
     */
    private CommentVO convertToVOWithNovelTitle(Comment comment) {
        // 查询用户昵称
        String nickname = null;
        if (comment.getUserId() != null) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                nickname = user.getNickname();
            }
        }

        // 查询小说标题
        String novelTitle = null;
        if (comment.getNovelId() != null) {
            Novel novel = novelMapper.selectById(comment.getNovelId());
            if (novel != null) {
                novelTitle = novel.getTitle();
            }
        }

        return CommentVO.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .nickname(nickname)
                .novelId(comment.getNovelId())
                .novelTitle(novelTitle)
                .content(comment.getContent())
                .createTime(comment.getCreateTime())
                .updateTime(comment.getUpdateTime())
                .build();
    }

    /**
     * 将 Comment 实体转换为 CommentVO
     */
    private CommentVO convertToVO(Comment comment) {
        // 查询用户昵称
        String nickname = null;
        if (comment.getUserId() != null) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                nickname = user.getNickname();
            }
        }

        return CommentVO.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .nickname(nickname)
                .novelId(comment.getNovelId())
                .content(comment.getContent())
                .createTime(comment.getCreateTime())
                .updateTime(comment.getUpdateTime())
                .build();
    }

    /**
     * 将 Comment 分页转换为 CommentVO 分页
     */
    private Page<CommentVO> convertToVOPage(Page<Comment> commentPage) {
        Page<CommentVO> voPage = new Page<>();
        voPage.setCurrent(commentPage.getCurrent());
        voPage.setSize(commentPage.getSize());
        voPage.setTotal(commentPage.getTotal());
        voPage.setPages(commentPage.getPages());

        List<CommentVO> records = commentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }
}