<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in statsList" :key="stat.key">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="32" color="#fff">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-title">{{ stat.title }}</p>
              <p class="stat-value" :style="{ color: stat.color }">
                {{ formatNumber(stats[stat.key as keyof AuthorStats]) }}
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="goToCreateNovel">
          <el-icon><Plus /></el-icon>
          创建新小说
        </el-button>
        <el-button size="large" @click="goToNovels">
          <el-icon><Reading /></el-icon>
          管理小说
        </el-button>
        <el-button size="large" @click="goToComments">
          <el-icon><ChatDotRound /></el-icon>
          查看评论
        </el-button>
      </div>
    </el-card>

    <!-- 最近更新的小说 -->
    <el-card class="recent-novels" style="margin-top: 20px;" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>我的小说</span>
          <el-button text @click="goToNovels">查看全部</el-button>
        </div>
      </template>
      <el-empty v-if="novels.length === 0" description="暂无小说，快去创建吧" />
      <el-table v-else :data="novels" style="width: 100%">
        <el-table-column prop="title" label="小说标题" min-width="200">
          <template #default="{ row }">
            <div class="novel-title-cell">
              <el-image
                :src="getImageUrl(row.coverImage)"
                fit="cover"
                style="width: 50px; height: 70px; border-radius: 4px;"
              />
              <span>{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="chapterCount" label="章节数" width="100" align="center" />
        <el-table-column prop="wordCount" label="字数" width="120" align="center">
          <template #default="{ row }">
            {{ formatNumber(row.wordCount) }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读量" width="120" align="center">
          <template #default="{ row }">
            {{ formatNumber(row.viewCount) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editNovel(row.id)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, ChatDotRound, Document, View, Plus } from '@element-plus/icons-vue'
import { get } from '@/api'

const router = useRouter()
const loading = ref(false)

interface AuthorStats {
  totalNovels: number
  totalChapters: number
  totalWordCount: number
  totalViewCount: number
  totalComments: number
  publishedNovels: number
  unpublishedNovels: number
}

interface Novel {
  id: number
  title: string
  coverImage: string
  chapterCount: number
  wordCount: number
  viewCount: number
  status: number
}

const stats = ref<AuthorStats>({
  totalNovels: 0,
  totalChapters: 0,
  totalWordCount: 0,
  totalViewCount: 0,
  totalComments: 0,
  publishedNovels: 0,
  unpublishedNovels: 0
})

const novels = ref<Novel[]>([])

const statsList = [
  { key: 'totalNovels', title: '小说总数', icon: 'Reading', color: '#409eff' },
  { key: 'totalChapters', title: '章节总数', icon: 'Document', color: '#67c23a' },
  { key: 'totalWordCount', title: '总字数', icon: 'Document', color: '#e6a23c' },
  { key: 'totalViewCount', title: '总阅读量', icon: 'View', color: '#f56c6c' }
]

// 获取图片完整URL
const getImageUrl = (coverImage: string | null) => {
  if (!coverImage) return '/default-cover.jpg'
  if (coverImage.startsWith('http')) return coverImage
  return import.meta.env.VITE_API_BASE_URL + coverImage
}

const formatNumber = (num: number | undefined) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}

const fetchStats = async () => {
  try {
    const result = await get<AuthorStats>('/author/dashboard/stats')
    stats.value = result
  } catch (error: any) {
    ElMessage.error(error.message || '获取统计数据失败')
  }
}

const fetchNovels = async () => {
  loading.value = true
  try {
    const result = await get<{ records: Novel[]; total: number }>('/author/novels', {
      params: {
        page: 1,
        size: 5
      }
    })
    novels.value = result.records || []
  } catch (error: any) {
    ElMessage.error(error.message || '获取小说列表失败')
  } finally {
    loading.value = false
  }
}

const goToCreateNovel = () => {
  router.push('/author/novels/create')
}

const goToNovels = () => {
  router.push('/author/novels')
}

const goToComments = () => {
  router.push('/author/comments')
}

const editNovel = (id: number) => {
  router.push(`/author/novels/edit/${id}`)
}

onMounted(() => {
  fetchStats()
  fetchNovels()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-title {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.stat-value {
  margin: 8px 0 0;
  font-size: 28px;
  font-weight: bold;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.novel-title-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
