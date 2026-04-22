<template>
  <div class="admin-dashboard">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h3>欢迎使用小说后台管理系统</h3>
        </div>
      </template>

      <div class="dashboard-content">
        <el-result
          icon="success"
          title="后台管理系统已就绪"
          sub-title="请使用左侧菜单开始管理操作"
        >
          <template #extra>
            <div class="stats" v-loading="loading">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-statistic title="用户总数" :value="stats.userCount" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="小说总数" :value="stats.novelCount" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="章节总数" :value="stats.chapterCount" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="评论总数" :value="stats.commentCount" />
                </el-col>
              </el-row>
            </div>

            <div class="quick-actions">
              <h4>快速操作</h4>
              <el-space wrap>
                <el-button type="primary" @click="goToNovels">管理小说</el-button>
                <el-button type="success" @click="goToChapters">管理章节</el-button>
                <el-button type="warning" @click="goToUsers">管理用户</el-button>
                <el-button type="info" @click="goToCategories">管理分类</el-button>
              </el-space>
            </div>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminStats } from '@/api/adminDashboard'
import type { AdminStats } from '@/api/adminDashboard'

const router = useRouter()
const loading = ref(false)

// 统计数据
const stats = ref<AdminStats>({
  userCount: 0,
  novelCount: 0,
  chapterCount: 0,
  commentCount: 0
})

// 加载统计数据
const loadStats = async () => {
  loading.value = true
  try {
    const data = await getAdminStats()
    stats.value = data
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const goToNovels = () => {
  router.push('/admin/novels')
}

const goToChapters = () => {
  router.push('/admin/chapters')
}

const goToUsers = () => {
  router.push('/admin/users')
}

const goToCategories = () => {
  router.push('/admin/categories')
}

// 页面加载时获取数据
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.welcome-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  text-align: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
}

.dashboard-content {
  text-align: center;
}

.stats {
  margin: 30px 0;
}

.quick-actions {
  margin-top: 40px;
}

.quick-actions h4 {
  margin-bottom: 20px;
  color: #666;
}
</style>
