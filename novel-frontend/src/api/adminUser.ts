import { get, put } from '.'
import type { UserVO, PageResult, AdminUserQueryParams } from '@/types'

/**
 * 管理员获取用户分页列表
 */
export const getAdminUsers = (params?: AdminUserQueryParams): Promise<PageResult<UserVO>> => {
  return get<PageResult<UserVO>>('/admin/users', { params })
}

/**
 * 管理员更新用户状态
 */
export const updateUserStatus = (id: number, status: number): Promise<void> => {
  return put<void>(`/admin/users/${id}/status`, { status })
}