package com.example.novel.controller;

import com.example.novel.common.core.Result;
import com.example.novel.vo.CategoryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类控制器测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 测试获取分类列表
     */
    @Test
    void testListAll() {
        // 发送GET请求
        ResponseEntity<Result<List<CategoryVO>>> response = restTemplate.exchange(
                "/categories",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<List<CategoryVO>>>() {}
        );

        // 验证响应状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证响应体
        Result<List<CategoryVO>> result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("操作成功", result.getMessage());

        // 验证数据
        List<CategoryVO> categories = result.getData();
        assertNotNull(categories);
        assertFalse(categories.isEmpty());

        // 验证分类数据格式
        CategoryVO firstCategory = categories.get(0);
        assertNotNull(firstCategory.getId());
        assertNotNull(firstCategory.getName());
        assertNotNull(firstCategory.getSortOrder());

        // 验证排序：sortOrder应该递增
        for (int i = 0; i < categories.size() - 1; i++) {
            assertTrue(categories.get(i).getSortOrder() <= categories.get(i + 1).getSortOrder(),
                    "分类应按sortOrder升序排列");
        }

        // 打印测试结果
        System.out.println("分类列表测试通过，共 " + categories.size() + " 个分类");
        categories.forEach(category -> System.out.println(
                "ID: " + category.getId() + ", 名称: " + category.getName() + ", 排序: " + category.getSortOrder()));
    }
}