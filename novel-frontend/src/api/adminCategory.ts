import { get, post, put, del } from '.'
import type { AdminCategoryVO, AdminCategoryCreateParams, AdminCategoryUpdateParams } from '@/types'

/**
 * 管理员获取分类列表（不分页）
 */
export const getAdminCategories = (): Promise<AdminCategoryVO[]> => {
  return get<AdminCategoryVO[]>('/admin/categories')
}

/**
 * 管理员创建分类
 */
export const createAdminCategory = (params: AdminCategoryCreateParams): Promise<number> => {
  return post<number>('/admin/categories', params)
}

/**
 * 管理员更新分类
 */
export const updateAdminCategory = (id: number, params: AdminCategoryUpdateParams): Promise<void> => {
  return put<void>(`/admin/categories/${id}`, params)
}

/**
 * 管理员删除分类
 */
export const deleteAdminCategory = (id: number): Promise<void> => {
  return del<void>(`/admin/categories/${id}`)
}