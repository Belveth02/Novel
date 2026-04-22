import { post, get } from '.'

// 用户登录响应类型
export interface UserLoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  role: string
  email: string
  phone: string
  avatar: string
}

// 用户信息响应类型
export interface UserInfoResponse {
  id: number
  username: string
  nickname: string
  role: string
  email: string
  phone: string
  avatar: string
  status: number
  createTime: string
  updateTime: string
}

// 用户注册请求参数
export interface UserRegisterParams {
  username: string
  password: string
  nickname?: string
  email?: string
  phone?: string
}

// 用户登录请求参数
export interface UserLoginParams {
  username: string
  password: string
}

/**
 * 用户注册
 */
export const userRegister = (params: UserRegisterParams): Promise<UserLoginResponse> => {
  return post<UserLoginResponse>('/auth/register', params)
}

/**
 * 用户登录
 */
export const userLogin = (params: UserLoginParams): Promise<UserLoginResponse> => {
  return post<UserLoginResponse>('/auth/login', params)
}

/**
 * 获取当前用户信息
 */
export const getUserInfo = (): Promise<UserInfoResponse> => {
  return get<UserInfoResponse>('/auth/info')
}

/**
 * 退出登录
 */
export const userLogout = (): Promise<void> => {
  return post<void>('/auth/logout')
}

/**
 * 检查是否已登录
 */
export const isLoggedIn = (): boolean => {
  const token = localStorage.getItem('user_token')
  return !!token
}