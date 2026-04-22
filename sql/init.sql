/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80037
 Source Host           : localhost:3306
 Source Schema         : novel_db

 Target Server Type    : MySQL
 Target Server Version : 80037
 File Encoding         : 65001

 Date: 22/04/2026 23:15:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小说分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '玄幻', '东方玄幻、异界大陆等', 1, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `category` VALUES (2, '仙侠', '修真、修仙、神话等', 2, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `category` VALUES (3, '都市', '都市生活、职场商战等', 3, '2026-04-18 06:30:55', '2026-04-20 06:12:01');
INSERT INTO `category` VALUES (4, '历史', '历史穿越、架空历史等', 4, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `category` VALUES (5, '科幻', '未来世界、星际文明等', 5, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `category` VALUES (6, '游戏', '网游、电竞、虚拟现实等', 6, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `category` VALUES (7, '言情', '现代言情、古代言情等', 7, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `category` VALUES (8, '悬疑', '侦探推理、灵异惊悚等', 8, '2026-04-18 06:30:55', '2026-04-18 06:30:55');

-- ----------------------------
-- Table structure for chapter
-- ----------------------------
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `novel_id` bigint UNSIGNED NOT NULL COMMENT '小说ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '章节标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '章节内容',
  `chapter_number` int NOT NULL COMMENT '章节序号',
  `word_count` int NOT NULL DEFAULT 0 COMMENT '章节字数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_novel_chapter`(`novel_id` ASC, `chapter_number` ASC) USING BTREE,
  INDEX `idx_novel_id`(`novel_id` ASC) USING BTREE,
  INDEX `idx_chapter_number`(`chapter_number` ASC) USING BTREE,
  INDEX `idx_chapter_novel_id`(`novel_id` ASC) USING BTREE,
  CONSTRAINT `fk_chapter_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter
-- ----------------------------
INSERT INTO `chapter` VALUES (1, 1, '第一章 少年', '东宝瓶洲，大骊王朝，龙泉郡。\n\n时值盛夏，烈日炎炎。\n\n郡城外的官道上，尘土飞扬，一队车马缓缓而行。\n\n为首的是一辆装饰简朴的马车，车帘掀起，露出一张清秀的少年脸庞。', 1, 83, '2026-04-18 06:30:55', '2026-04-20 05:14:55');
INSERT INTO `chapter` VALUES (2, 1, '第二章 剑胚', '陈平安跟着马队走了三天，终于抵达龙泉剑派的山门。\n\n山门前立着一块巨大的石碑，上面刻着\"龙泉\"两个大字，笔力遒劲，隐隐有剑气透出。', 2, 3200, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `chapter` VALUES (3, 2, '第一章 醒来', '周明瑞从梦中惊醒，猛地坐起身，大口喘气。\n\n额头满是冷汗，背后的睡衣也被汗水浸湿。\n\n他做了一个奇怪的梦，梦里有无数的触手、巨大的眼睛，还有低沉的呢喃声。', 1, 2800, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `chapter` VALUES (4, 2, '第二章 诡秘', '1111', 2, 4, '2026-04-20 23:09:59', '2026-04-20 23:09:59');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `novel_id` bigint UNSIGNED NOT NULL COMMENT '小说ID',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_novel_id`(`novel_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (12, 4, 1, '1', '2026-04-22 12:44:57', '2026-04-22 12:44:57');
INSERT INTO `comment` VALUES (13, 3, 1, '1', '2026-04-22 12:45:48', '2026-04-22 12:45:48');
INSERT INTO `comment` VALUES (14, 3, 2, '111', '2026-04-22 21:03:47', '2026-04-22 21:03:47');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `novel_id` bigint UNSIGNED NOT NULL COMMENT '小说ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_novel`(`user_id` ASC, `novel_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_novel_id`(`novel_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (15, 2, 1, '2026-04-22 12:35:19');
INSERT INTO `favorite` VALUES (16, 4, 1, '2026-04-22 12:46:05');
INSERT INTO `favorite` VALUES (17, 3, 1, '2026-04-22 12:46:19');
INSERT INTO `favorite` VALUES (18, 3, 2, '2026-04-22 21:11:04');

-- ----------------------------
-- Table structure for novel
-- ----------------------------
DROP TABLE IF EXISTS `novel`;
CREATE TABLE `novel`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '小说ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '小说标题',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作者',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '小说描述',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `word_count` int NOT NULL DEFAULT 0 COMMENT '总字数',
  `chapter_count` int NOT NULL DEFAULT 0 COMMENT '章节数',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '阅读次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_hot` tinyint NOT NULL DEFAULT 0 COMMENT '是否热门推荐：0-否，1-是',
  `hot_sort` int NOT NULL DEFAULT 0 COMMENT '热门排序（数值越大越靠前）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_author`(`author` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_novel_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_novel_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_hot`(`is_hot` ASC) USING BTREE,
  INDEX `idx_hot_sort`(`hot_sort` ASC) USING BTREE,
  CONSTRAINT `fk_novel_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小说表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of novel
-- ----------------------------
INSERT INTO `novel` VALUES (1, '剑来', '烽火戏诸侯', '/uploads/covers/66b56734-c608-4e97-9da9-3a07d96e84d3.jpg', '大千世界，无奇不有。我陈平安，唯有一剑，可搬山，倒海，降妖，镇魔，敕神，摘星，断江，摧城，开天！', 2, 1, 3283, 1200, 43, '2026-04-18 06:30:55', '2026-04-22 21:32:17', 1, 100);
INSERT INTO `novel` VALUES (2, '诡秘之主', '爱潜水的乌贼', '/uploads/covers/3774e386-59d6-4776-b450-66c4ef71d44a.jpg', '蒸汽与机械的浪潮中，谁能触及非凡？历史和黑暗的迷雾里，又是谁在耳语？我从诡秘中醒来，睁眼看见这个世界：枪械，大炮，巨舰，飞空艇，差分机；魔药，占卜，诅咒，倒吊人，封印物……光明依旧照耀，神秘从未远离，这是一段\"愚者\"的传说。', 5, 1, 2804, 2, 11, '2026-04-18 06:30:55', '2026-04-22 21:25:50', 1, 101);

-- ----------------------------
-- Table structure for reading_history
-- ----------------------------
DROP TABLE IF EXISTS `reading_history`;
CREATE TABLE `reading_history`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `novel_id` bigint UNSIGNED NOT NULL COMMENT '小说ID',
  `chapter_id` bigint UNSIGNED NOT NULL COMMENT '章节ID',
  `progress` int NOT NULL DEFAULT 0 COMMENT '阅读进度（百分比）',
  `last_read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_novel`(`user_id` ASC, `novel_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_novel_id`(`novel_id` ASC) USING BTREE,
  INDEX `idx_last_read_time`(`last_read_time` ASC) USING BTREE,
  INDEX `fk_history_chapter`(`chapter_id` ASC) USING BTREE,
  CONSTRAINT `fk_history_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_history_novel` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reading_history
-- ----------------------------
INSERT INTO `reading_history` VALUES (1, 2, 1, 2, 100, '2026-04-22 12:35:26', '2026-04-21 13:24:43');
INSERT INTO `reading_history` VALUES (2, 2, 2, 4, 100, '2026-04-22 11:15:54', '2026-04-21 13:27:18');
INSERT INTO `reading_history` VALUES (3, 3, 1, 2, 100, '2026-04-22 21:10:56', '2026-04-22 12:46:28');
INSERT INTO `reading_history` VALUES (4, 3, 2, 3, 100, '2026-04-22 21:11:06', '2026-04-22 21:11:02');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `role` enum('ADMIN','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '管理员', '$2a$10$FpKSxHjHQj9UOP8Zi/LsG.qi42cCGmlLbS2o2t6Bs7dPZx47dHyxS', 'ADMIN', 'admin@novel.com', NULL, NULL, 1, '2026-04-18 06:30:55', '2026-04-22 18:50:42');
INSERT INTO `user` VALUES (2, 'testuser', '测试用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVjVsq', 'USER', 'user@novel.com', NULL, NULL, 1, '2026-04-18 06:30:55', '2026-04-18 06:30:55');
INSERT INTO `user` VALUES (3, 'user', 'belveth', '$2a$10$gCfBjv4pFCGKJa42.X6r/eQBnGJcVxUNbV.al1ywA/20DwmCVW1ZC', 'USER', '0010@qq.com', '19892820620', '/uploads/avatars/dcd04d22-115d-42fc-9783-42967a517599.jpg', 1, '2026-04-22 10:16:08', '2026-04-22 21:04:18');
INSERT INTO `user` VALUES (4, 'bbb', 'bbb', '$2a$10$UwieFG3OKWftj0WS7TGOT.KIdLg5UuJ1ZEWyyBGHd3tBBdYA6VMy6', 'USER', NULL, NULL, NULL, 1, '2026-04-22 12:38:01', '2026-04-22 12:38:01');

SET FOREIGN_KEY_CHECKS = 1;
