import { get } from '.'
import type { ChapterVO, ChapterDetailVO, PageResult, ChapterQueryParams } from '@/types'

/**
 * 获取章节列表
 */
export const getChapters = (params: ChapterQueryParams): Promise<PageResult<ChapterVO>> => {
  return get<PageResult<ChapterVO>>('/chapters', { params })
}

/**
 * 获取章节详情（包含内容）
 */
export const getChapterDetail = (id: number): Promise<ChapterDetailVO> => {
  return get<ChapterDetailVO>(`/chapters/${id}`)
}