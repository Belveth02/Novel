package com.example.novel.controller.admin;

import com.example.novel.common.util.JwtUtil;
import com.example.novel.entity.User;
import com.example.novel.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdminAuthController 测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User mockAdminUser;

    @BeforeEach
    void setUp() {
        // 模拟管理员用户
        mockAdminUser = new User();
        mockAdminUser.setId(1L);
        mockAdminUser.setUsername("admin");
        mockAdminUser.setPassword(passwordEncoder.encode("admin123"));
        mockAdminUser.setNickname("管理员");
        mockAdminUser.setRole("ADMIN");
        mockAdminUser.setStatus(1);
        mockAdminUser.setAvatar("");
    }

    /**
     * 测试正确用户名密码登录 → 返回 token
     */
    @Test
    void testLoginSuccess() throws Exception {
        // 模拟查询用户
        when(userMapper.selectOne(any())).thenReturn(mockAdminUser);
        // 模拟生成token
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("mocked-jwt-token");

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin123");

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    /**
     * 测试错误密码登录 → 返回错误信息
     */
    @Test
    void testLoginWrongPassword() throws Exception {
        // 模拟查询用户
        when(userMapper.selectOne(any())).thenReturn(mockAdminUser);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "wrongpassword");

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // 业务异常返回200，但success为false
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    /**
     * 测试无 token 访问受保护端点 → 401
     */
    @Test
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        // 模拟 JwtInterceptor 会拦截请求并验证 token
        // 由于没有 token，应该返回 401
        // 注意：这里需要确保 JwtInterceptor 被正确配置，但 MockMvc 会加载整个应用上下文
        // 因此会触发 JwtInterceptor 的验证逻辑
        mockMvc.perform(get("/admin/novels"))
                .andExpect(status().isUnauthorized()); // 期望 401 状态码
    }

    /**
     * 测试用户不存在登录 → 返回错误信息
     */
    @Test
    void testLoginUserNotFound() throws Exception {
        // 模拟查询用户返回 null
        when(userMapper.selectOne(any())).thenReturn(null);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nonexistent");
        loginRequest.put("password", "anypassword");

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    /**
     * 测试用户被禁用登录 → 返回错误信息
     */
    @Test
    void testLoginUserDisabled() throws Exception {
        // 模拟被禁用的用户
        User disabledUser = new User();
        disabledUser.setId(2L);
        disabledUser.setUsername("disabled");
        disabledUser.setPassword(passwordEncoder.encode("password"));
        disabledUser.setRole("ADMIN");
        disabledUser.setStatus(0); // 禁用状态

        when(userMapper.selectOne(any())).thenReturn(disabledUser);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "disabled");
        loginRequest.put("password", "password");

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户已被禁用"));
    }

    /**
     * 测试非管理员用户登录 → 返回错误信息
     */
    @Test
    void testLoginNonAdminUser() throws Exception {
        // 模拟普通用户
        User normalUser = new User();
        normalUser.setId(3L);
        normalUser.setUsername("user");
        normalUser.setPassword(passwordEncoder.encode("password"));
        normalUser.setRole("USER"); // 普通用户角色
        normalUser.setStatus(1);

        when(userMapper.selectOne(any())).thenReturn(normalUser);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "password");

        mockMvc.perform(post("/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("权限不足，仅限管理员登录"));
    }
}