// 分类视图对象
export interface CategoryVO {
  id: number
  name: string
  description: string
  sortOrder: number
}

// 小说视图对象
export interface NovelVO {
  id: number
  title: string
  author: string
  coverImage: string
  description: string
  categoryId: number
  status: number
  wordCount: number
  chapterCount: number
  viewCount: number
  createTime: string
}

// 章节视图对象
export interface ChapterVO {
  id: number
  novelId: number
  title: string
  chapterNumber: number
  wordCount: number
  createTime: string
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 统一响应结果
export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 小说查询参数
export interface NovelQueryParams {
  page?: number
  size?: number
  categoryId?: number
  title?: string
  author?: string
  sortBy?: 'viewCount' | 'createTime' | 'wordCount'
  sortOrder?: 'asc' | 'desc'
}

// 章节查询参数
export interface ChapterQueryParams {
  novelId: number
  page?: number
  size?: number
}