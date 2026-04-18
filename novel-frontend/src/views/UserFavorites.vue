<template>
  <div class="favorites-page">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <h1>小说网站</h1>
        </div>
        <div class="nav">
          <el-button type="text" @click="$router.push('/')">首页</el-button>
          <el-button type="text" @click="$router.push('/novels')">全部小说</el-button>
          <el-button type="text" @click="$router.back()">返回</el-button>
        </div>
      </div>
    </el-header>

    <el-main class="main">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2>我的收藏</h2>
        <div class="favorite-count">
          <span v-if="favoriteCount !== null">共 {{ favoriteCount }} 个收藏</span>
          <span v-else>加载中...</span>
        </div>
      </div>

      <!-- 收藏列表 -->
      <div v-loading="loading" class="favorites-list">
        <div v-if="favorites.length === 0" class="empty">
          <el-empty description="暂无收藏">
            <el-button type="primary" @click="$router.push('/novels')">
              去发现小说
            </el-button>
          </el-empty>
        </div>
        <div v-else class="favorites-grid">
          <div
            v-for="item in favorites"
            :key="item.id"
            class="favorite-card"
            @click="goToNovelDetail(item.novelId)"
          >
            <el-image
              :src="item.novelCoverImage || 'https://via.placeholder.com/160x240?text=封面'"
              fit="cover"
              class="novel-cover"
            />
            <div class="favorite-info">
              <div class="favorite-header">
                <h4 class="novel-title">{{ item.novelTitle }}</h4>
                <el-button
                  type="danger"
                  size="small"
                  :loading="cancelLoading[item.novelId]"
                  @click.stop="handleCancelFavorite(item.novelId)"
                >
                  取消收藏
                </el-button>
              </div>
              <p class="novel-author">{{ item.novelAuthor }}</p>
              <p class="novel-description">{{ item.novelDescription?.slice(0, 80) }}...</p>
              <div class="novel-stats">
                <span><el-icon><View /></el-icon> {{ item.novelViewCount }}</span>
                <span><el-icon><Document /></el-icon> {{ item.novelChapterCount }}章</span>
                <span><el-icon><EditPen /></el-icon> {{ item.novelWordCount }}字</span>
              </div>
              <div class="favorite-meta">
                <span class="create-time">
                  <el-icon><Clock /></el-icon>
                  收藏于 {{ formatDate(item.createTime) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="favorites.length > 0" class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <p>© 2026 小说网站 - 仅供学习使用</p>
    </el-footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorites, getFavoriteCount, toggleFavorite } from '@/api/favorite'
import type { FavoriteVO, PageResult } from '@/types'
import {
  View,
  Document,
  EditPen,
  Clock
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 收藏数据
const favorites = ref<FavoriteVO[]>([])
const favoriteCount = ref<number | null>(null)
const total = ref(0)
const loading = ref(false)

// 取消收藏加载状态
const cancelLoading = ref<Record<number, boolean>>({})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 12
})

// 初始化临时用户ID
const initTempUserId = () => {
  const stored = localStorage.getItem('tempUserId')
  if (!stored) {
    const tempUserId = 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    localStorage.setItem('tempUserId', tempUserId)
  }
}

// 加载收藏数量
const loadFavoriteCount = async () => {
  try {
    favoriteCount.value = await getFavoriteCount()
  } catch (err) {
    console.error('加载收藏数量失败:', err)
    favoriteCount.value = null
  }
}

// 加载收藏列表
const loadFavorites = async () => {
  loading.value = true
  try {
    const result: PageResult<FavoriteVO> = await getFavorites(queryParams)
    favorites.value = result.records
    total.value = result.total
  } catch (err) {
    console.error('加载收藏列表失败:', err)
    favorites.value = []
    total.value = 0
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 取消收藏
const handleCancelFavorite = async (novelId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    cancelLoading.value[novelId] = true
    const result = await toggleFavorite(novelId)

    if (!result) {
      // result 为 false 表示取消收藏成功
      ElMessage.success('已取消收藏')
      // 重新加载列表和数量
      loadFavorites()
      loadFavoriteCount()
    } else {
      ElMessage.warning('操作异常')
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('取消收藏失败:', err)
      ElMessage.error('取消收藏失败')
    }
  } finally {
    cancelLoading.value[novelId] = false
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  loadFavorites()
}

const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  loadFavorites()
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 跳转到小说详情
const goToNovelDetail = (id: number) => {
  router.push(`/novels/${id}`)
}

// 初始化加载
onMounted(() => {
  initTempUserId()
  loadFavoriteCount()
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  cursor: pointer;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
  color: #409eff;
}

.nav .el-button {
  margin-left: 20px;
  font-size: 16px;
}

.main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
}

.page-header {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.favorite-count {
  font-size: 14px;
  color: #666;
  background: #f5f7fa;
  padding: 5px 15px;
  border-radius: 20px;
}

.favorites-list {
  min-height: 500px;
}

.empty {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.favorite-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.novel-cover {
  width: 100%;
  height: 240px;
}

.favorite-info {
  padding: 15px;
}

.favorite-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 10px;
}

.novel-title {
  font-size: 16px;
  margin: 0 10px 0 0;
  color: #333;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
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
  margin-bottom: 10px;
}

.novel-stats .el-icon {
  margin-right: 3px;
}

.favorite-meta {
  display: flex;
  justify-content: flex-end;
  font-size: 12px;
  color: #999;
}

.favorite-meta .el-icon {
  margin-right: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    padding: 10px 0;
  }

  .nav {
    margin-top: 10px;
  }

  .main {
    padding: 10px;
  }

  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
  }

  .novel-cover {
    height: 200px;
  }

  .favorite-header {
    flex-direction: column;
    gap: 10px;
  }

  .novel-title {
    margin-right: 0;
  }
}

@media (max-width: 480px) {
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .page-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>