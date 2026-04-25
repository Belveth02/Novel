package com.example.novel.controller.author;

import com.example.novel.common.core.Result;
import com.example.novel.dto.AuthorNovelCreateDTO;
import com.example.novel.dto.AuthorNovelQueryDTO;
import com.example.novel.dto.AuthorNovelUpdateDTO;
import com.example.novel.service.IAuthorNovelService;
import com.example.novel.vo.NovelVO;
import jakarta.servlet.http.HttpServletRequest;
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
 * 作者小说控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/author/novels")
public class AuthorNovelController {

    private final IAuthorNovelService authorNovelService;

    @Value("${app.upload.cover-path:uploads/covers/}")
    private String coverUploadPath;

    @Value("${app.upload.cover-url:/uploads/covers/}")
    private String coverUrlPrefix;

    /**
     * 分页查询作者自己的小说列表
     *
     * @param queryDTO 查询参数
     * @param request  HTTP请求
     * @return 分页结果
     */
    @GetMapping
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<NovelVO>> list(
            @Valid AuthorNovelQueryDTO queryDTO,
            HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者查询小说列表，作者ID: {}", authorUserId);
        return Result.success(authorNovelService.listByAuthor(authorUserId, queryDTO));
    }

    /**
     * 获取小说详情
     *
     * @param id      小说ID
     * @param request HTTP请求
     * @return 小说详情
     */
    @GetMapping("/{id}")
    public Result<NovelVO> get(@PathVariable Long id, HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者获取小说详情，ID: {}, 作者ID: {}", id, authorUserId);
        return Result.success(authorNovelService.getById(id, authorUserId));
    }

    /**
     * 创建小说
     *
     * @param createDTO 创建参数
     * @param request   HTTP请求
     * @return 小说ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AuthorNovelCreateDTO createDTO, HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者创建小说，标题: {}, 作者ID: {}", createDTO.getTitle(), authorUserId);
        Long id = authorNovelService.createNovel(authorUserId, createDTO);
        return Result.success(id);
    }

    /**
     * 更新小说
     *
     * @param id        小说ID
     * @param updateDTO 更新参数
     * @param request   HTTP请求
     * @return 空结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody AuthorNovelUpdateDTO updateDTO,
                               HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者更新小说，ID: {}, 作者ID: {}", id, authorUserId);
        authorNovelService.updateNovel(id, authorUserId, updateDTO);
        return Result.success();
    }

    /**
     * 删除小说
     *
     * @param id      小说ID
     * @param request HTTP请求
     * @return 空结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long authorUserId = (Long) request.getAttribute("userId");
        log.info("作者删除小说，ID: {}, 作者ID: {}", id, authorUserId);
        authorNovelService.deleteNovel(id, authorUserId);
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
        log.info("作者上传小说封面请求");

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

            log.info("作者封面上传成功，URL: {}", coverUrl);
            return Result.success("上传成功", coverUrl);

        } catch (IOException e) {
            log.error("作者封面上传失败", e);
            return Result.error("封面上传失败: " + e.getMessage());
        }
    }
}
