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
            <div class="stats">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-statistic title="用户总数" :value="0" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="小说总数" :value="0" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="章节总数" :value="0" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="评论总数" :value="0" />
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
import { useRouter } from 'vue-router'

const router = useRouter()

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