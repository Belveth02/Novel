import { post, get } from '.'

// 管理员登录响应类型
export interface AdminLoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  role: string
  avatar: string
}

// 登录请求参数
export interface AdminLoginParams {
  username: string
  password: string
}

/**
 * 管理员登录
 */
export const adminLogin = (params: AdminLoginParams): Promise<AdminLoginResponse> => {
  return post<AdminLoginResponse>('/admin/auth/login', params)
}

/**
 * 获取当前管理员信息
 */
export const getAdminInfo = (): Promise<AdminLoginResponse> => {
  return get<AdminLoginResponse>('/admin/auth/info', {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('admin_token')}`
    }
  })
}

/**
 * 检查是否已登录
 */
export const isAdminLoggedIn = (): boolean => {
  const token = localStorage.getItem('admin_token')
  return !!token
}

/**
 * 退出登录
 */
export const adminLogout = (): void => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_user')
}