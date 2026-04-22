import { get, put, post } from '.'
import type { UserVO, UserUpdateParams, ChangePasswordParams, CommentVO, PageResult } from '@/types'

/**
 * 获取当前用户信息
 */
export const getCurrentUser = (): Promise<UserVO> => {
  return get<UserVO>('/users/me')
}

/**
 * 更新用户信息
 */
export const updateUserInfo = (params: UserUpdateParams): Promise<UserVO> => {
  return put<UserVO>('/users/me', params)
}

/**
 * 修改密码
 */
export const changePassword = (params: ChangePasswordParams): Promise<boolean> => {
  return post<boolean>('/users/me/password', params)
}

/**
 * 上传头像
 */
export const uploadAvatar = (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('file', file)
  return post<string>('/users/me/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取当前用户的评论列表
 */
export const getMyComments = (params: {
  page?: number
  size?: number
}): Promise<PageResult<CommentVO>> => {
  return get<PageResult<CommentVO>>('/comments/my', { params })
}

/**
 * 获取当前用户评论数量
 */
export const getMyCommentCount = (): Promise<number> => {
  return get<number>('/users/me/comments/count')
}
