<template>
  <div class="novel-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>小说管理</span>
          <el-button type="primary" @click="goToCreate">
            <el-icon><Plus /></el-icon>
            创建小说
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索小说标题"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="novelList"
        style="width: 100%"
        border
      >
        <el-table-column label="封面" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="getImageUrl(row.coverImage)"
              fit="cover"
              style="width: 60px; height: 80px; border-radius: 4px"
              :preview-src-list="[getImageUrl(row.coverImage)]"
            />
          </template>
        </el-table-column>

        <el-table-column prop="title" label="小说标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />

        <el-table-column label="分类" width="100" align="center">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>

        <el-table-column label="章节/字数" width="150" align="center">
          <template #default="{ row }">
            <div>{{ row.chapterCount }} 章</div>
            <div style="font-size: 12px; color: #999">{{ formatWordCount(row.wordCount) }} 字</div>
          </template>
        </el-table-column>

        <el-table-column prop="viewCount" label="阅读量" width="100" align="center">
          <template #default="{ row }">
            {{ formatNumber(row.viewCount) }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" @click="handleChapters(row)">章节</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { get, del } from '@/api'

const router = useRouter()
const loading = ref(false)
const novelList = ref([])
const total = ref(0)
const categories = ref([])

const queryParams = ref({
  page: 1,
  size: 10,
  keyword: '',
  status: null as number | null
})

const fetchNovels = async () => {
  loading.value = true
  try {
    const result = await get('/author/novels', {
      params: queryParams.value
    })
    novelList.value = result.records || []
    total.value = result.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '获取小说列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const result = await get('/categories')
    categories.value = result || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取图片完整URL
const getImageUrl = (coverImage: string | null) => {
  if (!coverImage) return '/default-cover.jpg'
  if (coverImage.startsWith('http')) return coverImage
  return import.meta.env.VITE_API_BASE_URL + coverImage
}

const getCategoryName = (categoryId: number) => {
  const category = categories.value.find((c: any) => c.id === categoryId)
  return category?.name || '未知'
}

const formatNumber = (num: number) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}

const formatWordCount = (num: number) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchNovels()
}

const handleReset = () => {
  queryParams.value = {
    page: 1,
    size: 10,
    keyword: '',
    status: null
  }
  fetchNovels()
}

const handleSizeChange = (size: number) => {
  queryParams.value.size = size
  fetchNovels()
}

const handlePageChange = (page: number) => {
  queryParams.value.page = page
  fetchNovels()
}

const goToCreate = () => {
  router.push('/author/novels/create')
}

const handleEdit = (row: any) => {
  router.push(`/author/novels/edit/${row.id}`)
}

const handleChapters = (row: any) => {
  router.push(`/author/chapters?novelId=${row.id}`)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除小说《${row.title}》吗？此操作将同时删除所有章节和评论，不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await del(`/author/novels/${row.id}`)
      ElMessage.success('删除成功')
      fetchNovels()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

onMounted(() => {
  fetchCategories()
  fetchNovels()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
