package com.example.novel.common;

import com.example.novel.common.core.Result;
import com.example.novel.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通用组件测试类
 */
@SpringBootTest
class CommonComponentTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试Result成功响应
     */
    @Test
    void testResultSuccess() {
        // 测试无数据成功
        Result<Void> result1 = Result.success();
        assertEquals(Result.SUCCESS, result1.getCode());
        assertEquals("操作成功", result1.getMessage());
        assertNull(result1.getData());
        assertNotNull(result1.getTimestamp());
        assertTrue(result1.isSuccess());

        // 测试带数据成功
        Result<String> result2 = Result.success("test data");
        assertEquals(Result.SUCCESS, result2.getCode());
        assertEquals("操作成功", result2.getMessage());
        assertEquals("test data", result2.getData());
        assertTrue(result2.isSuccess());

        // 测试自定义消息成功
        Result<String> result3 = Result.success("自定义成功消息", "data");
        assertEquals(Result.SUCCESS, result3.getCode());
        assertEquals("自定义成功消息", result3.getMessage());
        assertEquals("data", result3.getData());
    }

    /**
     * 测试Result错误响应
     */
    @Test
    void testResultError() {
        // 测试默认错误
        Result<Void> result1 = Result.error("错误消息");
        assertEquals(Result.ERROR, result1.getCode());
        assertEquals("错误消息", result1.getMessage());
        assertNull(result1.getData());
        assertFalse(result1.isSuccess());

        // 测试自定义错误码
        Result<Void> result2 = Result.error(400, "参数错误");
        assertEquals(400, result2.getCode());
        assertEquals("参数错误", result2.getMessage());

        // 测试带数据错误
        Result<String> result3 = Result.error(400, "参数错误", "error details");
        assertEquals(400, result3.getCode());
        assertEquals("参数错误", result3.getMessage());
        assertEquals("error details", result3.getData());
    }

    /**
     * 测试Result JSON序列化/反序列化
     */
    @Test
    void testResultJsonSerialization() throws Exception {
        Result<String> original = Result.success("test data");
        String json = objectMapper.writeValueAsString(original);

        Result<?> deserialized = objectMapper.readValue(json, Result.class);

        assertEquals(original.getCode(), deserialized.getCode());
        assertEquals(original.getMessage(), deserialized.getMessage());
        // 注意：由于泛型擦除，data字段可能被反序列化为LinkedHashMap，这里只验证非空
        assertNotNull(deserialized.getData());
    }

    /**
     * 测试BusinessException构造
     */
    @Test
    void testBusinessException() {
        // 测试默认构造
        BusinessException ex1 = new BusinessException("业务错误");
        assertEquals(500, ex1.getCode());
        assertEquals("业务错误", ex1.getMessage());

        // 测试自定义错误码构造
        BusinessException ex2 = new BusinessException(400, "参数错误");
        assertEquals(400, ex2.getCode());
        assertEquals("参数错误", ex2.getMessage());

        // 测试带原因的构造
        Throwable cause = new IllegalArgumentException("原始异常");
        BusinessException ex3 = new BusinessException("业务错误", cause);
        assertEquals(500, ex3.getCode());
        assertEquals("业务错误", ex3.getMessage());
        assertEquals(cause, ex3.getCause());

        BusinessException ex4 = new BusinessException(400, "参数错误", cause);
        assertEquals(400, ex4.getCode());
        assertEquals("参数错误", ex4.getMessage());
        assertEquals(cause, ex4.getCause());
    }

    /**
     * 测试Result常量
     */
    @Test
    void testResultConstants() {
        assertEquals(200, Result.SUCCESS);
        assertEquals(500, Result.ERROR);
        assertEquals(400, Result.BAD_REQUEST);
        assertEquals(401, Result.UNAUTHORIZED);
        assertEquals(403, Result.FORBIDDEN);
        assertEquals(404, Result.NOT_FOUND);
    }
}