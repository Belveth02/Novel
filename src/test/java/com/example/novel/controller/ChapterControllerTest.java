package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.vo.ChapterVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 章节控制器测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChapterControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 测试分页查询章节列表
     */
    @Test
    void testListByNovelId() {
        // 先获取一个有效的小说ID
        ResponseEntity<Result<Page<com.example.novel.vo.NovelVO>>> novelResponse = restTemplate.exchange(
                "/novels?pageSize=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<Page<com.example.novel.vo.NovelVO>>>() {}
        );

        Result<Page<com.example.novel.vo.NovelVO>> novelResult = novelResponse.getBody();
        assertNotNull(novelResult);
        Page<com.example.novel.vo.NovelVO> novelPage = novelResult.getData();

        if (novelPage.getRecords().isEmpty()) {
            System.out.println("没有小说数据，跳过章节测试");
            return;
        }

        Long novelId = novelPage.getRecords().get(0).getId();

        // 测试查询章节
        ResponseEntity<Result<Page<ChapterVO>>> response = restTemplate.exchange(
                "/chapters?novelId=" + novelId + "&pageNum=1&pageSize=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<Page<ChapterVO>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Result<Page<ChapterVO>> result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isSuccess());

        Page<ChapterVO> page = result.getData();
        assertNotNull(page);
        assertTrue(page.getTotal() >= 0);

        // 验证章节数据
        if (!page.getRecords().isEmpty()) {
            ChapterVO chapter = page.getRecords().get(0);
            assertNotNull(chapter.getId());
            assertNotNull(chapter.getTitle());
            assertNotNull(chapter.getChapterNumber());
            assertEquals(novelId, chapter.getNovelId());
        }

        System.out.println("章节列表分页测试通过");
        System.out.println("小说ID: " + novelId);
        System.out.println("章节总数: " + page.getTotal());
        System.out.println("当前页章节数: " + page.getRecords().size());
    }
}