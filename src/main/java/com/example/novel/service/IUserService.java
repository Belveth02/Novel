package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminUserQueryDTO;
import com.example.novel.dto.UserLoginDTO;
import com.example.novel.dto.UserRegisterDTO;
import com.example.novel.vo.UserLoginVO;
import com.example.novel.vo.UserVO;

/**
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册参数
     * @return 用户登录信息（包含token）
     */
    UserLoginVO register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 用户登录信息（包含token）
     */
    UserLoginVO login(UserLoginDTO loginDTO);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 管理员分页查询用户列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    Page<UserVO> listAdminByPage(AdminUserQueryDTO queryDTO);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态：0-禁用，1-正常
     */
    void updateUserStatus(Long id, Integer status);
}