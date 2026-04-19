package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminUserQueryDTO;
import com.example.novel.vo.UserVO;

/**
 * 用户服务接口
 */
public interface IUserService {

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