package com.example.novel.controller.admin;

import com.example.novel.common.core.Result;
import com.example.novel.dto.AdminNovelCreateDTO;
import com.example.novel.dto.AdminNovelQueryDTO;
import com.example.novel.dto.AdminNovelUpdateDTO;
import com.example.novel.service.INovelService;
import com.example.novel.vo.NovelVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员小说控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/novels")
public class AdminNovelController {

    private final INovelService novelService;

    /**
     * 分页查询小说列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<NovelVO>> list(@Valid AdminNovelQueryDTO queryDTO) {
        log.info("查询小说列表，参数: {}", queryDTO);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<NovelVO> page = novelService.listAdminByPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取小说详情
     *
     * @param id 小说ID
     * @return 小说详情
     */
    @GetMapping("/{id}")
    public Result<NovelVO> get(@PathVariable Long id) {
        log.info("获取小说详情，ID: {}", id);
        NovelVO novel = novelService.getAdminById(id);
        return Result.success(novel);
    }

    /**
     * 新增小说
     *
     * @param createDTO 创建参数
     * @return 小说ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AdminNovelCreateDTO createDTO) {
        log.info("新增小说，标题: {}", createDTO.getTitle());
        Long id = novelService.createNovel(createDTO);
        return Result.success(id);
    }

    /**
     * 编辑小说
     *
     * @param id 小说ID
     * @param updateDTO 更新参数
     * @return 空结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AdminNovelUpdateDTO updateDTO) {
        log.info("编辑小说，ID: {}", id);
        novelService.updateNovel(id, updateDTO);
        return Result.success();
    }

    /**
     * 删除小说
     *
     * @param id 小说ID
     * @return 空结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除小说，ID: {}", id);
        novelService.deleteNovel(id);
        return Result.success();
    }
}