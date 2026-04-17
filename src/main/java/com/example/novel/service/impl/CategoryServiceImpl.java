package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.novel.entity.Category;
import com.example.novel.mapper.CategoryMapper;
import com.example.novel.service.ICategoryService;
import com.example.novel.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> listAll() {
        log.info("查询所有分类列表");

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSortOrder);

        List<Category> categories = categoryMapper.selectList(queryWrapper);
        log.info("查询到 {} 个分类", categories.size());

        // 转换为VO
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转VO
     */
    private CategoryVO convertToVO(Category category) {
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .sortOrder(category.getSortOrder())
                .build();
    }
}