package com.example.novel.service;

import com.example.novel.dto.AdminCategoryCreateDTO;
import com.example.novel.dto.AdminCategoryUpdateDTO;
import com.example.novel.vo.AdminCategoryVO;
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

    /**
     * 管理员查询所有分类（包含小说数量）
     */
    List<AdminCategoryVO> listAdminAll();

    /**
     * 管理员获取分类详情（包含小说数量）
     *
     * @param id 分类ID
     * @return 分类详情
     */
    AdminCategoryVO getAdminById(Long id);

    /**
     * 创建分类
     *
     * @param createDTO 创建参数
     * @return 分类ID
     */
    Long createCategory(AdminCategoryCreateDTO createDTO);

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param updateDTO 更新参数
     */
    void updateCategory(Long id, AdminCategoryUpdateDTO updateDTO);

    /**
     * 删除分类（若该分类下有小说，返回错误提示）
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}