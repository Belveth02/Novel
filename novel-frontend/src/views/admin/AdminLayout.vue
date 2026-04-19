<template>
  <div class="admin-layout">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>小说后台</h2>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="/admin/novels">
          <el-icon><Notebook /></el-icon>
          <span>小说管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/chapters">
          <el-icon><Document /></el-icon>
          <span>章节管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/categories">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="navbar-left"></div>

        <div class="navbar-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar" />
              <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="content-wrapper">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Odometer,
  Notebook,
  Document,
  User,
  Folder,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'
import { adminLogout } from '@/api/admin'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref({
  username: '',
  nickname: '',
  avatar: ''
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 加载用户信息
const loadUserInfo = () => {
  const userStr = localStorage.getItem('admin_user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      userInfo.value = {
        username: user.username || '',
        nickname: user.nickname || '',
        avatar: user.avatar || ''
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'logout') {
    handleLogout()
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    adminLogout()
    ElMessage.success('已退出登录')
    router.push('/admin/login')
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 左侧边栏 */
.sidebar {
  width: 220px;
  background-color: #304156;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #1f2d3d;
}

.sidebar-header h2 {
  margin: 0;
  color: white;
  font-size: 18px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.navbar {
  height: 60px;
  padding: 0 20px;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-info .username {
  margin: 0 8px;
  font-size: 14px;
}

/* 页面内容 */
.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}
</style>