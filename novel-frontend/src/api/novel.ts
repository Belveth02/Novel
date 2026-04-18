import { get } from '.'
import type { NovelVO, PageResult, NovelQueryParams } from '@/types'

/**
 * 获取小说分页列表
 */
export const getNovels = (params?: NovelQueryParams): Promise<PageResult<NovelVO>> => {
  return get<PageResult<NovelVO>>('/novels', { params })
}

/**
 * 获取小说详情
 */
export const getNovelById = (id: number): Promise<NovelVO> => {
  return get<NovelVO>(`/novels/${id}`)
}

/**
 * 获取热门推荐小说
 */
export const getHotNovels = (): Promise<NovelVO[]> => {
  return get<NovelVO[]>('/novels/hot')
}