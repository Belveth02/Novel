import { get, post, put, del } from '.'
import type { ChapterVO, ChapterDetailVO, PageResult, AdminChapterQueryParams, AdminChapterCreateParams, AdminChapterUpdateParams } from '@/types'

/**
 * 管理员获取章节分页列表
 */
export const getAdminChapters = (params: AdminChapterQueryParams): Promise<PageResult<ChapterVO>> => {
  return get<PageResult<ChapterVO>>('/admin/chapters', { params })
}

/**
 * 管理员获取章节详情
 */
export const getAdminChapterById = (id: number): Promise<ChapterDetailVO> => {
  return get<ChapterDetailVO>(`/admin/chapters/${id}`)
}

/**
 * 管理员创建章节
 */
export const createAdminChapter = (params: AdminChapterCreateParams): Promise<number> => {
  return post<number>('/admin/chapters', params)
}

/**
 * 管理员更新章节
 */
export const updateAdminChapter = (id: number, params: AdminChapterUpdateParams): Promise<void> => {
  return put<void>(`/admin/chapters/${id}`, params)
}

/**
 * 管理员删除章节
 */
export const deleteAdminChapter = (id: number): Promise<void> => {
  return del<void>(`/admin/chapters/${id}`)
}