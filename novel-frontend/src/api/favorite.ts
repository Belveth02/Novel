import { get, post } from '.'
import type { FavoriteVO, PageResult } from '@/types'

/**
 * 收藏/取消收藏
 */
export const toggleFavorite = (novelId: number): Promise<boolean> => {
  return post<boolean>('/favorites/toggle', null, { params: { novelId } })
}

/**
 * 检查收藏状态
 */
export const checkFavorite = (novelId: number): Promise<boolean> => {
  return get<boolean>('/favorites/check', { params: { novelId } })
}

/**
 * 获取用户的收藏列表
 */
export const getFavorites = (params: {
  pageNum?: number
  pageSize?: number
}): Promise<PageResult<FavoriteVO>> => {
  return get<PageResult<FavoriteVO>>('/favorites', { params })
}

/**
 * 获取用户的收藏数量
 */
export const getFavoriteCount = (): Promise<number> => {
  return get<number>('/favorites/count')
}