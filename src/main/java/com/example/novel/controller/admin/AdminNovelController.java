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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    @Value("${app.upload.cover-path:uploads/covers/}")
    private String coverUploadPath;

    @Value("${app.upload.cover-url:/uploads/covers/}")
    private String coverUrlPrefix;

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

    /**
     * 上传小说封面
     *
     * @param file 封面文件
     * @return 封面URL
     */
    @PostMapping("/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        log.info("上传小说封面请求");

        // 验证文件
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 验证文件大小（限制2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("文件大小不能超过2MB");
        }

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(coverUploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID() + extension;

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 构建URL
            String coverUrl = coverUrlPrefix + filename;

            log.info("封面上传成功，URL: {}", coverUrl);
            return Result.success("上传成功", coverUrl);

        } catch (IOException e) {
            log.error("封面上传失败", e);
            return Result.error("封面上传失败: " + e.getMessage());
        }
    }
}