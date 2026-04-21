import { get, post, put, del } from '.'
import type { NovelVO, PageResult, AdminNovelQueryParams, AdminNovelCreateParams, AdminNovelUpdateParams } from '@/types'

/**
 * 管理员获取小说分页列表
 */
export const getAdminNovels = (params?: AdminNovelQueryParams): Promise<PageResult<NovelVO>> => {
  return get<PageResult<NovelVO>>('/admin/novels', { params })
}

/**
 * 管理员获取小说详情
 */
export const getAdminNovelById = (id: number): Promise<NovelVO> => {
  return get<NovelVO>(`/admin/novels/${id}`)
}

/**
 * 管理员创建小说
 */
export const createAdminNovel = (params: AdminNovelCreateParams): Promise<number> => {
  return post<number>('/admin/novels', params)
}

/**
 * 管理员更新小说
 */
export const updateAdminNovel = (id: number, params: AdminNovelUpdateParams): Promise<void> => {
  return put<void>(`/admin/novels/${id}`, params)
}

/**
 * 管理员删除小说
 */
export const deleteAdminNovel = (id: number): Promise<void> => {
  return del<void>(`/admin/novels/${id}`)
}