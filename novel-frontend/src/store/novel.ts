import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getHotNovels } from '@/api/novel'
import type { NovelVO } from '@/types'

export const useNovelStore = defineStore('novel', () => {
  // 热门推荐
  const hotNovels = ref<NovelVO[]>([])
  const hotLoading = ref(false)
  const hotError = ref<string | null>(null)

  // 缓存的小说详情
  const novelCache = ref<Map<number, NovelVO>>(new Map())

  // 获取热门推荐
  const fetchHotNovels = async () => {
    if (hotNovels.value.length > 0) {
      return hotNovels.value
    }

    hotLoading.value = true
    hotError.value = null
    try {
      hotNovels.value = await getHotNovels()
      return hotNovels.value
    } catch (err) {
      hotError.value = err instanceof Error ? err.message : '加载热门推荐失败'
      throw err
    } finally {
      hotLoading.value = false
    }
  }

  // 获取小说详情（带缓存）
  const getNovelFromCache = (id: number) => {
    return novelCache.value.get(id)
  }

  const cacheNovel = (novel: NovelVO) => {
    novelCache.value.set(novel.id, novel)
  }

  const clearNovelCache = () => {
    novelCache.value.clear()
  }

  const clearHotNovels = () => {
    hotNovels.value = []
  }

  return {
    // 热门推荐
    hotNovels,
    hotLoading,
    hotError,
    fetchHotNovels,
    clearHotNovels,

    // 缓存
    getNovelFromCache,
    cacheNovel,
    clearNovelCache
  }
})