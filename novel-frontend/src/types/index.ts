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
  isHot?: number
  hotSort?: number
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

// 章节详情视图对象（包含内容）
export interface ChapterDetailVO {
  id: number
  novelId: number
  novelTitle: string
  title: string
  content: string
  chapterNumber: number
  wordCount: number
  prevChapterId?: number
  nextChapterId?: number
  createTime: string
}

// 阅读记录视图对象
export interface ReadingHistoryVO {
  id: number
  userId: number
  novelId: number
  novelTitle: string
  novelCoverImage: string
  chapterId: number
  chapterTitle: string
  chapterNumber: number
  progress: number
  lastReadTime: string
  createTime: string
}

// 阅读记录数据传输对象
export interface ReadingHistoryDTO {
  userId?: number
  novelId: number
  chapterId: number
  progress: number
}

// 收藏视图对象
export interface FavoriteVO {
  id: number
  userId: number
  novelId: number
  novelTitle: string
  novelCoverImage: string
  novelAuthor: string
  novelDescription: string
  novelWordCount: number
  novelChapterCount: number
  novelViewCount: number
  createTime: string
}

// 评论视图对象
export interface CommentVO {
  id: number
  userId: number
  username?: string
  nickname?: string
  avatar?: string
  novelId: number
  novelTitle?: string
  content: string
  createTime: string
  updateTime?: string
}

// 章节查询参数
export interface ChapterQueryParams {
  novelId: number
  page?: number
  size?: number
}

// 管理员小说查询参数
export interface AdminNovelQueryParams {
  keyword?: string
  page?: number
  size?: number
  categoryId?: number
  status?: number
}

// 管理员小说创建参数
export interface AdminNovelCreateParams {
  title: string
  author: string
  coverImage?: string
  description?: string
  categoryId: number
  status: number
}

// 管理员小说更新参数
export interface AdminNovelUpdateParams {
  title?: string
  author?: string
  coverImage?: string
  description?: string
  categoryId?: number
  status?: number
  isHot?: number
  hotSort?: number
}

// 管理员章节查询参数
export interface AdminChapterQueryParams {
  novelId: number
  page?: number
  size?: number
}

// 管理员章节创建参数
export interface AdminChapterCreateParams {
  novelId: number
  title: string
  content: string
}

// 管理员章节更新参数
export interface AdminChapterUpdateParams {
  title?: string
  content?: string
}

// 用户视图对象
export interface UserVO {
  id: number
  username: string
  nickname: string
  role: 'ADMIN' | 'USER'
  email: string
  phone: string
  avatar: string
  status: number
  createTime: string
  updateTime: string
}

// 用户更新参数
export interface UserUpdateParams {
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
}

// 修改密码参数
export interface ChangePasswordParams {
  oldPassword: string
  newPassword: string
}

// 管理员用户查询参数
export interface AdminUserQueryParams {
  keyword?: string
  page?: number
  size?: number
}

// 管理员分类视图对象（包含小说数量）
export interface AdminCategoryVO {
  id: number
  name: string
  description: string
  sortOrder: number
  novelCount: number
  createTime: string
  updateTime: string
}

// 管理员分类创建参数
export interface AdminCategoryCreateParams {
  name: string
  description?: string
  sortOrder?: number
}

// 管理员分类更新参数
export interface AdminCategoryUpdateParams {
  name?: string
  description?: string
  sortOrder?: number
}