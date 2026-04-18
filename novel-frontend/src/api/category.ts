import { get } from '.'
import type { CategoryVO } from '@/types'

/**
 * 获取分类列表
 */
export const getCategories = (): Promise<CategoryVO[]> => {
  return get<CategoryVO[]>('/categories')
}