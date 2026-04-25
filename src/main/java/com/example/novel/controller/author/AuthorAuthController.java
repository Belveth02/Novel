package com.example.novel.controller.author;

import com.example.novel.common.constant.RoleConstant;
import com.example.novel.common.core.Result;
import com.example.novel.common.util.JwtUtil;
import com.example.novel.dto.UserLoginDTO;
import com.example.novel.service.IUserService;
import com.example.novel.vo.UserLoginVO;
import com.example.novel.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作者认证控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/author/auth")
public class AuthorAuthController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 作者登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        log.info("作者登录请求，用户名: {}", loginDTO.getUsername());

        // 调用用户服务进行登录验证
        UserLoginVO loginVO = userService.login(loginDTO);

        // 验证用户角色是否为作者或管理员
        if (!RoleConstant.AUTHOR.equals(loginVO.getRole()) && !RoleConstant.ADMIN.equals(loginVO.getRole())) {
            log.warn("用户 {} 不是作者角色，无法登录作者后台", loginDTO.getUsername());
            return Result.error("权限不足，仅限作者登录");
        }

        // 构建用户信息
        UserVO userVO = UserVO.builder()
                .id(loginVO.getUserId())
                .username(loginVO.getUsername())
                .nickname(loginVO.getNickname())
                .avatar(loginVO.getAvatar())
                .role(loginVO.getRole())
                .email(loginVO.getEmail())
                .phone(loginVO.getPhone())
                .token(loginVO.getToken())
                .build();

        log.info("作者 {} 登录成功", loginDTO.getUsername());
        return Result.success("登录成功", userVO);
    }

    /**
     * 获取当前登录作者信息
     *
     * @param request HTTP请求
     * @return 作者信息
     */
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取当前作者信息，ID: {}", userId);

        UserVO userVO = userService.getUserInfo(userId);
        if (userVO == null) {
            return Result.error("用户不存在");
        }

        return Result.success(userVO);
    }
}
