package com.example.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.dto.NovelQueryDTO;
import com.example.novel.vo.NovelVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 小说服务测试类
 */
@SpringBootTest
class NovelServiceTest {

    @Autowired
    private INovelService novelService;

    /**
     * 测试分页查询返回格式正确
     */
    @Test
    void testListByPage() {
        // 构造查询参数
        NovelQueryDTO queryDTO = new NovelQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        queryDTO.setSortBy("viewCount");
        queryDTO.setSortOrder("desc");

        // 调用服务
        Page<NovelVO> page = novelService.listByPage(queryDTO);

        // 验证分页结构
        assertNotNull(page);
        assertNotNull(page.getRecords());
        assertTrue(page.getCurrent() >= 1);
        assertTrue(page.getSize() >= 0);
        assertTrue(page.getTotal() >= 0);
        assertTrue(page.getPages() >= 0);

        // 验证记录格式
        for (NovelVO novel : page.getRecords()) {
            assertNotNull(novel.getId());
            assertNotNull(novel.getTitle());
            assertNotNull(novel.getAuthor());
            assertNotNull(novel.getCategoryId());
            assertNotNull(novel.getStatus());
            assertNotNull(novel.getCreateTime());

            // 状态应该是上架（1）
            assertEquals(1, novel.getStatus(), "小说状态应为上架（1）");

            // 阅读量应为非负数
            assertTrue(novel.getViewCount() >= 0);

            // 章节数应为非负数
            assertTrue(novel.getChapterCount() >= 0);

            // 字数应为非负数
            assertTrue(novel.getWordCount() >= 0);
        }

        // 输出测试结果
        System.out.println("分页查询测试通过");
        System.out.println("当前页: " + page.getCurrent());
        System.out.println("每页大小: " + page.getSize());
        System.out.println("总记录数: " + page.getTotal());
        System.out.println("总页数: " + page.getPages());
        System.out.println("当前页记录数: " + page.getRecords().size());
    }

    /**
     * 测试不存在的id返回null
     */
    @Test
    void testGetByIdNotFound() {
        // 使用一个不可能存在的ID（例如 Long.MAX_VALUE）
        Long nonExistentId = Long.MAX_VALUE;

        // 调用服务
        NovelVO novel = novelService.getById(nonExistentId);

        // 应该返回null
        assertNull(novel, "不存在的ID应该返回null");

        System.out.println("不存在的ID测试通过，返回null");
    }

    /**
     * 测试存在的id返回正确的小说信息
     */
    @Test
    void testGetByIdExists() {
        // 先查询第一页，获取一个存在的ID
        NovelQueryDTO queryDTO = new NovelQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(1);
        Page<NovelVO> page = novelService.listByPage(queryDTO);

        if (!page.getRecords().isEmpty()) {
            NovelVO firstNovel = page.getRecords().get(0);
            Long existId = firstNovel.getId();

            // 调用服务
            NovelVO novel = novelService.getById(existId);

            // 验证返回的小说不为空且ID匹配
            assertNotNull(novel);
            assertEquals(existId, novel.getId());
            assertEquals(firstNovel.getTitle(), novel.getTitle());
            assertEquals(firstNovel.getAuthor(), novel.getAuthor());
            assertEquals(firstNovel.getCategoryId(), novel.getCategoryId());
            assertEquals(firstNovel.getStatus(), novel.getStatus());

            System.out.println("存在的ID测试通过，返回正确的小说信息");
            System.out.println("小说ID: " + novel.getId());
            System.out.println("小说标题: " + novel.getTitle());
            System.out.println("作者: " + novel.getAuthor());
        } else {
            System.out.println("没有可用的测试数据，跳过存在的ID测试");
        }
    }

    /**
     * 测试热门小说推荐
     */
    @Test
    void testListHotNovels() {
        int limit = 5;

        // 调用服务
        Page<NovelVO> page = novelService.listHotNovels(limit);

        // 验证结果
        assertNotNull(page);
        assertNotNull(page.getRecords());
        assertTrue(page.getRecords().size() <= limit);

        // 验证热门小说按阅读量降序排列
        if (page.getRecords().size() > 1) {
            for (int i = 0; i < page.getRecords().size() - 1; i++) {
                assertTrue(page.getRecords().get(i).getViewCount() >=
                           page.getRecords().get(i + 1).getViewCount(),
                           "热门小说应按阅读量降序排列");
            }
        }

        System.out.println("热门小说推荐测试通过");
        System.out.println("请求数量: " + limit);
        System.out.println("返回数量: " + page.getRecords().size());
        page.getRecords().forEach(novel ->
            System.out.println("  - " + novel.getTitle() + " (阅读量: " + novel.getViewCount() + ")")
        );
    }
}