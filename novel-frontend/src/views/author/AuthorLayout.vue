<template>
  <div class="author-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <h3 v-if="!isCollapsed">作者后台</h3>
        <el-icon v-else :size="24"><User /></el-icon>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        background-color="#1a1a2e"
        text-color="#b8c5d6"
        active-text-color="#409eff"
      >
        <el-menu-item index="/author/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>

        <el-menu-item index="/author/novels">
          <el-icon><Reading /></el-icon>
          <template #title>小说管理</template>
        </el-menu-item>

        <el-menu-item index="/author/chapters">
          <el-icon><Document /></el-icon>
          <template #title>章节管理</template>
        </el-menu-item>

        <el-menu-item index="/author/comments">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>评论管理</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button
          type="text"
          :icon="isCollapsed ? Expand : Fold"
          @click="toggleSidebar"
          class="collapse-btn"
        >
          {{ isCollapsed ? '展开' : '收起' }}
        </el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container" :class="{ expanded: isCollapsed }">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <h2>{{ pageTitle }}</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">返回首页</el-dropdown-item>
                <el-dropdown-item command="profile">个人设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  DataLine,
  Reading,
  Document,
  ChatDotRound,
  Fold,
  Expand,
  ArrowDown
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/author/dashboard': '数据概览',
    '/author/novels': '小说管理',
    '/author/novels/create': '创建小说',
    '/author/chapters': '章节管理',
    '/author/comments': '评论管理'
  }
  return titles[route.path] || '作者后台'
})

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'home':
      router.push('/')
      break
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.clearUserInfo()
        localStorage.removeItem('author_token')
        ElMessage.success('已退出登录')
        router.push('/author/login')
      })
      break
  }
}
</script>

<style scoped>
.author-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: #1a1a2e;
  color: #fff;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 100;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h3 {
  margin: 0;
  color: #fff;
}

:deep(.el-menu) {
  border-right: none;
  flex: 1;
}

.sidebar-footer {
  padding: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-btn {
  color: #b8c5d6;
  width: 100%;
}

.collapse-btn:hover {
  color: #fff;
}

.main-container {
  flex: 1;
  margin-left: 220px;
  transition: margin-left 0.3s;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  min-height: 100vh;
}

.main-container.expanded {
  margin-left: 64px;
}

.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 99;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.username {
  font-size: 14px;
  color: #333;
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
