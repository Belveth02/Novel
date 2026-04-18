import { get } from '.'
import type { ChapterVO, PageResult, ChapterQueryParams } from '@/types'

/**
 * 获取章节列表
 */
export const getChapters = (params: ChapterQueryParams): Promise<PageResult<ChapterVO>> => {
  return get<PageResult<ChapterVO>>('/chapters', { params })
}