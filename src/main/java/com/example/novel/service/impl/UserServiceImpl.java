package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminUserQueryDTO;
import com.example.novel.entity.User;
import com.example.novel.mapper.UserMapper;
import com.example.novel.service.IUserService;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserVO> listAdminByPage(AdminUserQueryDTO queryDTO) {
        log.info("管理员分页查询用户列表, 参数: {}", queryDTO);

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 关键词筛选（用户名/邮箱）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword)
            );
        }

        // 按创建时间降序排列
        queryWrapper.orderByDesc(User::getCreateTime);

        // 执行分页查询
        Page<User> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        log.info("查询到 {} 条用户记录，共 {} 条", userPage.getRecords().size(), userPage.getTotal());
        return convertToVOPage(userPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        log.info("更新用户状态，ID: {}, 状态: {}", id, status);

        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            log.warn("用户不存在，ID: {}", id);
            throw new BusinessException("用户不存在");
        }

        // 禁止修改 ADMIN 角色用户的状态
        if ("ADMIN".equals(user.getRole())) {
            log.warn("禁止修改 ADMIN 角色用户的状态，用户ID: {}", id);
            throw new BusinessException("禁止修改管理员状态");
        }

        // 检查状态是否已为目标状态
        if (status.equals(user.getStatus())) {
            log.info("用户状态未变化，ID: {}, 当前状态: {}", id, status);
            return;
        }

        // 更新状态
        user.setStatus(status);
        int rows = userMapper.updateById(user);
        if (rows != 1) {
            log.error("更新用户状态失败，ID: {}", id);
            throw new BusinessException("更新用户状态失败");
        }

        log.info("用户状态更新成功，ID: {}, 新状态: {}", id, status);
    }

    /**
     * 将 User 实体转换为 UserVO
     */
    private UserVO convertToVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }

    /**
     * 将 User 分页转换为 UserVO 分页
     */
    private Page<UserVO> convertToVOPage(Page<User> userPage) {
        Page<UserVO> voPage = new Page<>();
        voPage.setCurrent(userPage.getCurrent());
        voPage.setSize(userPage.getSize());
        voPage.setTotal(userPage.getTotal());
        voPage.setPages(userPage.getPages());

        List<UserVO> records = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }
}