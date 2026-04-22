<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-container">
        <div class="header-left">
          <router-link to="/" class="logo">小说阅读</router-link>
          <nav class="nav-menu">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/novels" class="nav-item">小说列表</router-link>
          </nav>
        </div>
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="32" :src="getFullAvatarUrl(userStore.avatar) || undefined">
                  {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ userStore.nickname || userStore.username }}</span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="favorites">
                    <el-icon><star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item command="profile">
                    <el-icon><user /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><switch-button /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="auth-link">登录</router-link>
            <router-link to="/register" class="auth-link">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="app-main">
      <!-- 分类导航 -->
      <section class="section category-section">
        <div class="section-header">
          <h2>分类浏览</h2>
        </div>
        <div v-loading="categoryLoading" class="category-list">
          <el-tag
            v-for="category in categories"
            :key="category.id"
            class="category-tag"
            :type="selectedCategoryId === category.id ? 'primary' : 'info'"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </el-tag>
        </div>
      </section>

      <!-- 热门推荐 -->
      <section class="section hot-section">
        <div class="section-header">
          <h2>热门推荐</h2>
        </div>
        <div v-loading="hotLoading" class="hot-list">
          <el-carousel :interval="4000" height="300px">
            <el-carousel-item v-for="novel in hotNovels" :key="novel.id">
              <div class="hot-item" @click="goToNovelDetail(novel.id)">
                <el-image
                  :src="getFullCoverUrl(novel.coverImage) || 'https://via.placeholder.com/200x300?text=封面'"
                  fit="cover"
                  class="hot-cover"
                />
                <div class="hot-info">
                  <h3>{{ novel.title }}</h3>
                  <p class="author">{{ novel.author }}</p>
                  <p class="description">{{ novel.description }}</p>
                  <div class="stats">
                    <span><el-icon><View /></el-icon> {{ novel.viewCount }}</span>
                    <span><el-icon><Document /></el-icon> {{ novel.chapterCount }}章</span>
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </section>

      <!-- 最新小说 -->
      <section class="section latest-section">
        <div class="section-header">
          <h2>最新上架</h2>
        </div>
        <div v-loading="novelLoading" class="novel-grid">
          <div
            v-for="novel in novels"
            :key="novel.id"
            class="novel-card"
            @click="goToNovelDetail(novel.id)"
          >
            <el-image
              :src="getFullCoverUrl(novel.coverImage) || 'https://via.placeholder.com/160x240?text=封面'"
              fit="cover"
              class="novel-cover"
            />
            <div class="novel-info">
              <h4 class="novel-title">{{ novel.title }}</h4>
              <p class="novel-author">{{ novel.author }}</p>
              <p class="novel-description">{{ novel.description?.slice(0, 50) }}...</p>
              <div class="novel-stats">
                <span><el-icon><View /></el-icon> {{ novel.viewCount }}</span>
                <span><el-icon><Document /></el-icon> {{ novel.chapterCount }}章</span>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </section>
    </main>

    <!-- 底部 -->
    <footer class="app-footer">
      <p>小说阅读网站 &copy; 2026</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, Star, User, SwitchButton, View, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getCategories } from '@/api/category'
import { getNovels, getHotNovels } from '@/api/novel'
import type { CategoryVO, NovelVO, NovelQueryParams } from '@/types'

const router = useRouter()
const userStore = useUserStore()

// 获取完整头像 URL
const getFullAvatarUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 拼接完整 URL: http://localhost:8080/api + /uploads/avatars/xxx.jpg
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseUrl}${url}`
}

// 获取完整封面 URL
const getFullCoverUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 拼接完整 URL: http://localhost:8080/api + /uploads/covers/xxx.jpg
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseUrl}${url}`
}

// 页面加载时恢复用户状态
onMounted(() => {
  userStore.restoreFromStorage()
})

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

// 分类数据
const categories = ref<CategoryVO[]>([])
const categoryLoading = ref(false)
const selectedCategoryId = ref<number | undefined>()

// 热门推荐
const hotNovels = ref<NovelVO[]>([])
const hotLoading = ref(false)

// 最新小说
const novels = ref<NovelVO[]>([])
const total = ref(0)
const novelLoading = ref(false)
const queryParams = reactive<NovelQueryParams>({
  page: 1,
  size: 12,
  sortBy: 'createTime',
  sortOrder: 'desc'
})

// 加载分类
const loadCategories = async () => {
  categoryLoading.value = true
  try {
    categories.value = await getCategories()
  } catch (error) {
    console.error('加载分类失败:', error)
  } finally {
    categoryLoading.value = false
  }
}

// 加载热门推荐
const loadHotNovels = async () => {
  hotLoading.value = true
  try {
    const result: any = await getHotNovels()
    // 后端返回分页对象 {records: [...], total: ...}
    if (result && result.records) {
      hotNovels.value = result.records
    } else {
      hotNovels.value = Array.isArray(result) ? result : []
    }
  } catch (error) {
    console.error('加载热门推荐失败:', error)
  } finally {
    hotLoading.value = false
  }
}

// 加载小说列表
const loadNovels = async () => {
  novelLoading.value = true
  try {
    const result = await getNovels({
      ...queryParams,
      categoryId: selectedCategoryId.value
    })
    novels.value = result.records
    total.value = result.total
  } catch (error) {
    console.error('加载小说列表失败:', error)
  } finally {
    novelLoading.value = false
  }
}

// 选择分类
const selectCategory = (categoryId?: number) => {
  selectedCategoryId.value = categoryId === selectedCategoryId.value ? undefined : categoryId
  queryParams.page = 1
  loadNovels()
}

// 分页处理
const handleSizeChange = (size: number) => {
  queryParams.size = size
  queryParams.page = 1
  loadNovels()
}

const handleCurrentChange = (page: number) => {
  queryParams.page = page
  loadNovels()
}

// 跳转到小说详情
const goToNovelDetail = (id: number) => {
  router.push(`/novels/${id}`)
}

// 监听查询参数变化
watch(
  () => selectedCategoryId.value,
  () => {
    queryParams.page = 1
    loadNovels()
  }
)

// 初始化加载
onMounted(() => {
  loadCategories()
  loadHotNovels()
  loadNovels()
})
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 30px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: #333;
  text-decoration: none;
  padding: 8px 0;
  position: relative;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #409eff;
}

.nav-item.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.username {
  color: #333;
  font-size: 14px;
}

.auth-link {
  color: #409eff;
  text-decoration: none;
  padding: 8px 16px;
  border: 1px solid #409eff;
  border-radius: 4px;
  transition: all 0.3s;
}

.auth-link:hover {
  background-color: #409eff;
  color: white;
}

.app-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.app-footer {
  background: #333;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.app-footer p {
  margin: 0;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section {
  margin-bottom: 40px;
}

.section-header {
  margin-bottom: 20px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.category-tag {
  cursor: pointer;
  font-size: 14px;
  padding: 8px 16px;
  transition: all 0.3s;
}

.category-tag:hover {
  transform: translateY(-2px);
}

.hot-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.hot-item {
  display: flex;
  height: 100%;
  cursor: pointer;
}

.hot-cover {
  width: 200px;
  height: 300px;
}

.hot-info {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hot-info h3 {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: #333;
}

.hot-info .author {
  font-size: 16px;
  color: #666;
  margin-bottom: 15px;
}

.hot-info .description {
  font-size: 14px;
  color: #999;
  line-height: 1.6;
  margin-bottom: 20px;
}

.stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.stats .el-icon {
  margin-right: 5px;
}

.novel-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.novel-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.novel-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.novel-cover {
  width: 100%;
  height: 240px;
}

.novel-info {
  padding: 15px;
}

.novel-title {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #333;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.novel-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.novel-description {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.novel-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.novel-stats .el-icon {
  margin-right: 3px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 10px;
  }

  .header-left {
    gap: 15px;
  }

  .logo {
    font-size: 16px;
  }

  .nav-menu {
    gap: 10px;
  }

  .nav-item {
    font-size: 14px;
  }

  .auth-link {
    padding: 6px 12px;
    font-size: 14px;
  }

  .app-main {
    padding: 10px;
  }

  .hot-item {
    flex-direction: column;
    height: auto;
  }

  .hot-cover {
    width: 100%;
    height: 200px;
  }

  .hot-info {
    padding: 20px;
  }

  .hot-info h3 {
    font-size: 20px;
  }

  .novel-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }

  .novel-cover {
    height: 200px;
  }
}

@media (max-width: 480px) {
  .novel-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
