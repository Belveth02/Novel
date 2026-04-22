import { get, post, del } from '.'
import type { CommentVO, PageResult } from '@/types'

/**
 * 发布评论
 */
export const addComment = (novelId: number, content: string): Promise<number> => {
  return post<number>('/comments', null, {
    params: { novelId, content }
  })
}

/**
 * 获取小说的评论列表
 */
export const getComments = (params: {
  novelId: number
  pageNum?: number
  pageSize?: number
}): Promise<PageResult<CommentVO>> => {
  return get<PageResult<CommentVO>>('/comments', { params })
}

/**
 * 删除评论
 */
export const deleteComment = (commentId: number): Promise<boolean> => {
  return del<boolean>(`/comments/${commentId}`)
}

/**
 * 获取小说的评论数量
 */
export const getCommentCount = (novelId: number): Promise<number> => {
  return get<number>('/comments/count', { params: { novelId } })
}

/**
 * 获取当前用户的所有评论
 */
export const getMyComments = (params: {
  page?: number
  size?: number
}): Promise<PageResult<CommentVO>> => {
  return get<PageResult<CommentVO>>('/comments/my', { params })
}