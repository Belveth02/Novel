-- 小说网站数据库初始化脚本
-- MySQL 8.0.37
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci

-- 如果数据库不存在则创建
CREATE DATABASE IF NOT EXISTS `novel_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `novel_db`;
SET NAMES utf8mb4;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `role` ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 小说分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小说分类表';

-- 小说表
DROP TABLE IF EXISTS `novel`;
CREATE TABLE `novel` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '小说ID',
    `title` VARCHAR(100) NOT NULL COMMENT '小说标题',
    `author` VARCHAR(50) NOT NULL COMMENT '作者',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
    `description` TEXT COMMENT '小说描述',
    `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `word_count` INT NOT NULL DEFAULT 0 COMMENT '总字数',
    `chapter_count` INT NOT NULL DEFAULT 0 COMMENT '章节数',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '阅读次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_author` (`author`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_view_count` (`view_count`),
    CONSTRAINT `fk_novel_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小说表';

-- 章节表
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `novel_id` BIGINT UNSIGNED NOT NULL COMMENT '小说ID',
    `title` VARCHAR(100) NOT NULL COMMENT '章节标题',
    `content` LONGTEXT NOT NULL COMMENT '章节内容',
    `chapter_number` INT NOT NULL COMMENT '章节序号',
    `word_count` INT NOT NULL DEFAULT 0 COMMENT '章节字数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_novel_chapter` (`novel_id`, `chapter_number`),
    KEY `idx_novel_id` (`novel_id`),
    KEY `idx_chapter_number` (`chapter_number`),
    CONSTRAINT `fk_chapter_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='章节表';

-- 阅读记录表
DROP TABLE IF EXISTS `reading_history`;
CREATE TABLE `reading_history` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `novel_id` BIGINT UNSIGNED NOT NULL COMMENT '小说ID',
    `chapter_id` BIGINT UNSIGNED NOT NULL COMMENT '章节ID',
    `progress` INT NOT NULL DEFAULT 0 COMMENT '阅读进度（百分比）',
    `last_read_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后阅读时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_novel` (`user_id`, `novel_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_novel_id` (`novel_id`),
    KEY `idx_last_read_time` (`last_read_time`),
    CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_history_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_history_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='阅读记录表';

-- 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `novel_id` BIGINT UNSIGNED NOT NULL COMMENT '小说ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_novel` (`user_id`, `novel_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_novel_id` (`novel_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_favorite_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';

-- 评论表
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `novel_id` BIGINT UNSIGNED NOT NULL COMMENT '小说ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_novel_id` (`novel_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comment_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评论表';

-- 插入初始分类数据
INSERT INTO `category` (`name`, `description`, `sort_order`) VALUES
('玄幻', '东方玄幻、异界大陆等', 1),
('仙侠', '修真、修仙、神话等', 2),
('都市', '都市生活、职场商战等', 3),
('历史', '历史穿越、架空历史等', 4),
('科幻', '未来世界、星际文明等', 5),
('游戏', '网游、电竞、虚拟现实等', 6),
('言情', '现代言情、古代言情等', 7),
('悬疑', '侦探推理、灵异惊悚等', 8);

-- 插入测试用户（密码均为123456的BCrypt加密值）
INSERT INTO `user` (`username`, `nickname`, `password`, `role`, `email`, `status`) VALUES
('admin', '管理员', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'admin@novel.com', 1),
('testuser', '测试用户', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', 'user@novel.com', 1);

-- 插入测试小说
INSERT INTO `novel` (`title`, `author`, `description`, `category_id`, `word_count`, `chapter_count`) VALUES
('剑来', '烽火戏诸侯', '大千世界，无奇不有。我陈平安，唯有一剑，可搬山，倒海，降妖，镇魔，敕神，摘星，断江，摧城，开天！', 1, 8500000, 1200),
('诡秘之主', '爱潜水的乌贼', '蒸汽与机械的浪潮中，谁能触及非凡？历史和黑暗的迷雾里，又是谁在耳语？我从诡秘中醒来，睁眼看见这个世界：枪械，大炮，巨舰，飞空艇，差分机；魔药，占卜，诅咒，倒吊人，封印物……光明依旧照耀，神秘从未远离，这是一段"愚者"的传说。', 5, 4500000, 800);

-- 插入测试章节
INSERT INTO `chapter` (`novel_id`, `title`, `content`, `chapter_number`, `word_count`) VALUES
(1, '第一章 少年', '东宝瓶洲，大骊王朝，龙泉郡。\n\n时值盛夏，烈日炎炎。\n\n郡城外的官道上，尘土飞扬，一队车马缓缓而行。\n\n为首的是一辆装饰简朴的马车，车帘掀起，露出一张清秀的少年脸庞。', 1, 3500),
(1, '第二章 剑胚', '陈平安跟着马队走了三天，终于抵达龙泉剑派的山门。\n\n山门前立着一块巨大的石碑，上面刻着"龙泉"两个大字，笔力遒劲，隐隐有剑气透出。', 2, 3200),
(2, '第一章 醒来', '周明瑞从梦中惊醒，猛地坐起身，大口喘气。\n\n额头满是冷汗，背后的睡衣也被汗水浸湿。\n\n他做了一个奇怪的梦，梦里有无数的触手、巨大的眼睛，还有低沉的呢喃声。', 1, 2800);

-- 显示表结构信息
SHOW TABLES;

-- 显示各表记录数
SELECT
    'user' AS table_name, COUNT(*) AS record_count FROM `user`
UNION ALL
SELECT 'category', COUNT(*) FROM `category`
UNION ALL
SELECT 'novel', COUNT(*) FROM `novel`
UNION ALL
SELECT 'chapter', COUNT(*) FROM `chapter`
UNION ALL
SELECT 'reading_history', COUNT(*) FROM `reading_history`
UNION ALL
SELECT 'favorite', COUNT(*) FROM `favorite`
UNION ALL
SELECT 'comment', COUNT(*) FROM `comment`;