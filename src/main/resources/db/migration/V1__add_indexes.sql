-- 为 novel 表的 category_id 字段添加索引
CREATE INDEX idx_novel_category_id ON novel(category_id);

-- 为 novel 表的 status 字段添加索引
CREATE INDEX idx_novel_status ON novel(status);

-- 为 chapter 表的 novel_id 字段添加索引
CREATE INDEX idx_chapter_novel_id ON chapter(novel_id);