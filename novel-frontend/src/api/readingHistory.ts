import { get, post } from '.'
import type { ReadingHistoryVO, PageResult, ReadingHistoryDTO } from '@/types'

/**
 * 保存阅读记录
 */
export const saveReadingHistory = (data: ReadingHistoryDTO): Promise<void> => {
  return post<void>('/reading-history', data)
}

/**
 * 获取用户的阅读记录列表
 */
export const getReadingHistory = (params: {
  pageNum?: number
  pageSize?: number
}): Promise<PageResult<ReadingHistoryVO>> => {
  return get<PageResult<ReadingHistoryVO>>('/reading-history', { params })
}

/**
 * 获取用户对某本小说的阅读记录
 */
export const getReadingHistoryByNovel = (novelId: number): Promise<ReadingHistoryVO> => {
  return get<ReadingHistoryVO>(`/reading-history/novel/${novelId}`)
}