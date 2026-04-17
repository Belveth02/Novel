package com.example.novel.service;

import com.example.novel.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface ICategoryService {

    /**
     * 查询所有分类（按排序序号升序）
     */
    List<CategoryVO> listAll();
}