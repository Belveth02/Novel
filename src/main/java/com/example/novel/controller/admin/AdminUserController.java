package com.example.novel.controller.admin;

import com.example.novel.common.core.Result;
import com.example.novel.dto.AdminUserQueryDTO;
import com.example.novel.dto.AdminUserStatusUpdateDTO;
import com.example.novel.service.IUserService;
import com.example.novel.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final IUserService userService;

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserVO>> list(@Valid AdminUserQueryDTO queryDTO) {
        log.info("查询用户列表，参数: {}", queryDTO);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserVO> page = userService.listAdminByPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 修改用户状态
     *
     * @param id 用户ID
     * @param updateDTO 状态更新参数
     * @return 空结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody AdminUserStatusUpdateDTO updateDTO) {
        log.info("修改用户状态，ID: {}, 状态: {}", id, updateDTO.getStatus());
        userService.updateUserStatus(id, updateDTO.getStatus());
        return Result.success();
    }
}