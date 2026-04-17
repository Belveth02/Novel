package com.example.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.novel.common.core.Result;
import com.example.novel.vo.NovelVO;
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
 * 小说控制器测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NovelControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 测试分页查询小说列表
     */
    @Test
    void testListByPage() {
        // 发送GET请求
        ResponseEntity<Result<Page<NovelVO>>> response = restTemplate.exchange(
                "/novels?pageNum=1&pageSize=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<Page<NovelVO>>>() {}
        );

        // 验证响应状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证响应体
        Result<Page<NovelVO>> result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("操作成功", result.getMessage());

        // 验证分页数据
        Page<NovelVO> page = result.getData();
        assertNotNull(page);
        assertTrue(page.getTotal() >= 0);
        assertNotNull(page.getRecords());

        // 打印测试结果
        System.out.println("小说列表分页测试通过");
        System.out.println("总记录数: " + page.getTotal());
        System.out.println("当前页记录数: " + page.getRecords().size());
        System.out.println("总页数: " + page.getPages());
    }

    /**
     * 测试获取小说详情
     */
    @Test
    void testGetById() {
        // 先查询列表获取一个有效的小说ID
        ResponseEntity<Result<Page<NovelVO>>> listResponse = restTemplate.exchange(
                "/novels?pageSize=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<Page<NovelVO>>>() {}
        );

        Result<Page<NovelVO>> listResult = listResponse.getBody();
        assertNotNull(listResult);
        Page<NovelVO> page = listResult.getData();

        if (page.getRecords().isEmpty()) {
            System.out.println("没有小说数据，跳过详情测试");
            return;
        }

        Long novelId = page.getRecords().get(0).getId();

        // 测试获取详情
        ResponseEntity<Result<NovelVO>> response = restTemplate.exchange(
                "/novels/" + novelId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<NovelVO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Result<NovelVO> result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isSuccess());

        NovelVO novelVO = result.getData();
        assertNotNull(novelVO);
        assertEquals(novelId, novelVO.getId());
        assertNotNull(novelVO.getTitle());
        assertNotNull(novelVO.getAuthor());

        System.out.println("小说详情测试通过");
        System.out.println("小说标题: " + novelVO.getTitle());
        System.out.println("作者: " + novelVO.getAuthor());
    }

    /**
     * 测试获取热门小说推荐
     */
    @Test
    void testListHotNovels() {
        ResponseEntity<Result<Page<NovelVO>>> response = restTemplate.exchange(
                "/novels/hot?limit=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<Page<NovelVO>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Result<Page<NovelVO>> result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isSuccess());

        Page<NovelVO> page = result.getData();
        assertNotNull(page);
        assertTrue(page.getRecords().size() <= 5);

        System.out.println("热门小说推荐测试通过");
        System.out.println("推荐数量: " + page.getRecords().size());
    }
}