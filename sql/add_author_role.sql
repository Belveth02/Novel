-- 添加作者角色到用户表
-- 执行此脚本修改role字段，添加AUTHOR选项

ALTER TABLE `user`
MODIFY COLUMN `role` enum('ADMIN','USER','AUTHOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户，AUTHOR-作者';

-- 添加author_user_id字段到novel表，用于关联作者用户
-- 该字段用于标识小说的实际作者用户ID

ALTER TABLE `novel`
ADD COLUMN `author_user_id` bigint UNSIGNED NULL COMMENT '作者用户ID，关联user表' AFTER `author`;

-- 添加外键约束
ALTER TABLE `novel`
ADD CONSTRAINT `fk_novel_author_user` FOREIGN KEY (`author_user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- 添加索引
CREATE INDEX `idx_author_user_id` ON `novel`(`author_user_id`);
