import { get } from '.'

// 统计数据类型
export interface AdminStats {
  userCount: number
  novelCount: number
  chapterCount: number
  commentCount: number
}

/**
 * 获取仪表盘统计数据
 */
export const getAdminStats = (): Promise<AdminStats> => {
  return get<AdminStats>('/admin/dashboard/stats')
}
