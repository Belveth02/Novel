package com.example.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.novel.dto.AdminCategoryCreateDTO;
import com.example.novel.dto.AdminCategoryUpdateDTO;
import com.example.novel.entity.Category;
import com.example.novel.entity.Novel;
import com.example.novel.mapper.CategoryMapper;
import com.example.novel.mapper.NovelMapper;
import com.example.novel.service.ICategoryService;
import com.example.novel.common.exception.BusinessException;
import com.example.novel.vo.AdminCategoryVO;
import com.example.novel.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final NovelMapper novelMapper;

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

    @Override
    public List<AdminCategoryVO> listAdminAll() {
        log.info("管理员查询所有分类列表（包含小说数量）");

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSortOrder);

        List<Category> categories = categoryMapper.selectList(queryWrapper);
        log.info("查询到 {} 个分类", categories.size());

        // 转换为AdminCategoryVO，并统计小说数量
        return categories.stream()
                .map(this::convertToAdminVO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminCategoryVO getAdminById(Long id) {
        log.info("管理员获取分类详情，ID: {}", id);

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在，ID: {}", id);
            throw new BusinessException("分类不存在");
        }

        return convertToAdminVO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(AdminCategoryCreateDTO createDTO) {
        log.info("创建分类，名称: {}", createDTO.getName());

        // 验证分类名称唯一性
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, createDTO.getName());
        Long count = categoryMapper.selectCount(wrapper);
        if (count > 0) {
            log.warn("分类名称已存在: {}", createDTO.getName());
            throw new BusinessException("分类名称已存在");
        }

        // 构建实体
        Category category = Category.builder()
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .sortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 插入数据库
        int rows = categoryMapper.insert(category);
        if (rows != 1) {
            log.error("创建分类失败，名称: {}", createDTO.getName());
            throw new BusinessException("创建分类失败");
        }

        log.info("分类创建成功，ID: {}, 名称: {}", category.getId(), category.getName());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, AdminCategoryUpdateDTO updateDTO) {
        log.info("更新分类，ID: {}, 参数: {}", id, updateDTO);

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在，ID: {}", id);
            throw new BusinessException("分类不存在");
        }

        // 验证分类名称唯一性（如果修改了名称）
        if (updateDTO.getName() != null && !updateDTO.getName().equals(category.getName())) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getName, updateDTO.getName())
                   .ne(Category::getId, id);
            Long count = categoryMapper.selectCount(wrapper);
            if (count > 0) {
                log.warn("分类名称已存在: {}", updateDTO.getName());
                throw new BusinessException("分类名称已存在");
            }
        }

        // 更新字段
        boolean updated = false;
        if (updateDTO.getName() != null) {
            category.setName(updateDTO.getName());
            updated = true;
        }
        if (updateDTO.getDescription() != null) {
            category.setDescription(updateDTO.getDescription());
            updated = true;
        }
        if (updateDTO.getSortOrder() != null) {
            category.setSortOrder(updateDTO.getSortOrder());
            updated = true;
        }

        if (updated) {
            category.setUpdateTime(LocalDateTime.now());
            int rows = categoryMapper.updateById(category);
            if (rows != 1) {
                log.error("更新分类失败，ID: {}", id);
                throw new BusinessException("更新分类失败");
            }
            log.info("分类更新成功，ID: {}", id);
        } else {
            log.info("分类信息未变化，ID: {}", id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        log.info("删除分类，ID: {}", id);

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在，ID: {}", id);
            throw new BusinessException("分类不存在");
        }

        // 检查是否有小说关联
        LambdaQueryWrapper<Novel> novelWrapper = new LambdaQueryWrapper<>();
        novelWrapper.eq(Novel::getCategoryId, id);
        Long novelCount = novelMapper.selectCount(novelWrapper);
        if (novelCount > 0) {
            log.warn("无法删除分类，该分类下有 {} 篇小说关联，ID: {}", novelCount, id);
            throw new BusinessException("该分类下有小说关联，无法删除");
        }

        int rows = categoryMapper.deleteById(id);
        if (rows != 1) {
            log.error("删除分类失败，ID: {}", id);
            throw new BusinessException("删除分类失败");
        }

        log.info("分类删除成功，ID: {}, 名称: {}", id, category.getName());
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

    /**
     * 实体转AdminVO（包含小说数量）
     */
    private AdminCategoryVO convertToAdminVO(Category category) {
        // 统计该分类下的小说数量
        LambdaQueryWrapper<Novel> novelWrapper = new LambdaQueryWrapper<>();
        novelWrapper.eq(Novel::getCategoryId, category.getId());
        Long novelCount = novelMapper.selectCount(novelWrapper);

        return AdminCategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .sortOrder(category.getSortOrder())
                .novelCount(novelCount)
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }
}