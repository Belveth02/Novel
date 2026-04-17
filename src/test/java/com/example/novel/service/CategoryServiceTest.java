package com.example.novel.service;

import com.example.novel.vo.CategoryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类服务测试类
 */
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 测试查询所有分类
     */
    @Test
    void testListAll() {
        // 调用服务
        List<CategoryVO> categories = categoryService.listAll();

        // 验证结果非空
        assertNotNull(categories);
        assertFalse(categories.isEmpty());

        // 验证分类数量至少为初始化数据量（8个）
        assertTrue(categories.size() >= 8, "分类数量应至少为初始化数据量");

        // 验证分类字段完整性
        for (CategoryVO category : categories) {
            assertNotNull(category.getId());
            assertNotNull(category.getName());
            assertNotNull(category.getSortOrder());
            // description可能为null，不做校验
        }

        // 验证排序
        for (int i = 0; i < categories.size() - 1; i++) {
            assertTrue(categories.get(i).getSortOrder() <= categories.get(i + 1).getSortOrder(),
                    "分类应按sortOrder升序排列");
        }

        // 输出测试结果
        System.out.println("分类服务测试通过，共 " + categories.size() + " 个分类");
        System.out.println("分类列表（按排序序号）：");
        categories.forEach(category -> System.out.printf("  [%d] %s (排序: %d)%n",
                category.getId(), category.getName(), category.getSortOrder()));
    }
}