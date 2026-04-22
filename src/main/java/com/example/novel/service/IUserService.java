package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.AdminUserQueryDTO;
import com.example.novel.dto.ChangePasswordDTO;
import com.example.novel.dto.UserLoginDTO;
import com.example.novel.dto.UserRegisterDTO;
import com.example.novel.dto.UserUpdateDTO;
import com.example.novel.vo.UserLoginVO;
import com.example.novel.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param updateDTO 更新参数
     * @return 更新后的用户信息
     */
    UserVO updateUser(Long userId, UserUpdateDTO updateDTO);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param passwordDTO 密码参数
     * @return true-修改成功，false-修改失败
     */
    boolean changePassword(Long userId, ChangePasswordDTO passwordDTO);

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(Long userId, MultipartFile file);
}