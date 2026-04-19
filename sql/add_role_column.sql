-- 为user表添加role字段
ALTER TABLE `user`
ADD COLUMN `role` ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户' AFTER `password`;

-- 更新现有用户的角色
UPDATE `user` SET `role` = 'ADMIN' WHERE `username` = 'admin';
UPDATE `user` SET `role` = 'USER' WHERE `username` = 'testuser';