package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.common.util.JwtUtil;
import com.example.novel.dto.AdminUserQueryDTO;
import com.example.novel.dto.UserLoginDTO;
import com.example.novel.dto.UserRegisterDTO;
import com.example.novel.entity.User;
import com.example.novel.mapper.UserMapper;
import com.example.novel.service.IUserService;
import com.example.novel.vo.UserLoginVO;
import com.example.novel.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO register(UserRegisterDTO registerDTO) {
        log.info("用户注册请求，用户名: {}", registerDTO.getUsername());

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        User existingUser = userMapper.selectOne(usernameWrapper);
        if (existingUser != null) {
            log.warn("用户名已存在: {}", registerDTO.getUsername());
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在（如果提供了邮箱）
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty()) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, registerDTO.getEmail());
            User existingEmail = userMapper.selectOne(emailWrapper);
            if (existingEmail != null) {
                log.warn("邮箱已被注册: {}", registerDTO.getEmail());
                throw new BusinessException("该邮箱已被注册");
            }
        }

        // 构建用户实体
        User user = User.builder()
                .username(registerDTO.getUsername())
                .nickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role("USER")
                .email(registerDTO.getEmail())
                .phone(registerDTO.getPhone())
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 保存用户
        int rows = userMapper.insert(user);
        if (rows != 1) {
            log.error("用户注册失败，用户名: {}", registerDTO.getUsername());
            throw new BusinessException("用户注册失败");
        }

        log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建响应
        return UserLoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 用户登录
     */
    @Override
    @Transactional(readOnly = true)
    public UserLoginVO login(UserLoginDTO loginDTO) {
        log.info("用户登录请求，用户名: {}", loginDTO.getUsername());

        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() != 1) {
            log.warn("登录失败，用户已被禁用: {}", loginDTO.getUsername());
            throw new BusinessException("该账号已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败，密码错误: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());

        // 构建响应
        return UserLoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 获取用户信息
     */
    @Override
    @Transactional(readOnly = true)
    public UserVO getUserInfo(Long userId) {
        log.info("获取用户信息，用户ID: {}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在，用户ID: {}", userId);
            throw new BusinessException("用户不存在");
        }

        return convertToVO(user);
    }

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