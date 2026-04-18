import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCategories } from '@/api/category'
import type { CategoryVO } from '@/types'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref<CategoryVO[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchCategories = async () => {
    if (categories.value.length > 0) {
      return categories.value
    }

    loading.value = true
    error.value = null
    try {
      categories.value = await getCategories()
      return categories.value
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getCategoryById = (id: number) => {
    return categories.value.find(category => category.id === id)
  }

  const clearCategories = () => {
    categories.value = []
  }

  return {
    categories,
    loading,
    error,
    fetchCategories,
    getCategoryById,
    clearCategories
  }
})