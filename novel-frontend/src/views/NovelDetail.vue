<template>
  <div class="novel-detail-page">
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
      <!-- 加载状态 -->
      <div v-if="loading" class="loading">
        <el-skeleton :rows="10" animated />
      </div>

      <!-- 小说详情 -->
      <div v-else-if="novel" class="novel-detail">
        <el-row :gutter="40">
          <!-- 封面 -->
          <el-col :xs="24" :sm="8" :md="6">
            <div class="cover-section">
              <el-image
                :src="getFullCoverUrl(novelData.coverImage) || 'https://via.placeholder.com/300x450?text=封面'"
                fit="cover"
                class="cover-image"
              />
              <div class="cover-actions">
                <el-button type="primary" size="large" class="read-btn" @click="startReading">
                  <el-icon><Reading /></el-icon>
                  开始阅读
                </el-button>
                <el-button
                  :type="isFavorited ? 'warning' : 'info'"
                  size="large"
                  class="collect-btn"
                  :loading="favoriteLoading"
                  @click="handleFavorite"
                >
                  <el-icon><Star /></el-icon>
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
          </el-col>

          <!-- 小说信息 -->
          <el-col :xs="24" :sm="16" :md="18">
            <div class="info-section">
              <h1 class="novel-title">{{ novelData.title }}</h1>
              <div class="novel-meta">
                <span class="author">
                  <el-icon><User /></el-icon>
                  {{ novelData.author }}
                </span>
                <el-tag v-if="novelData.status === 1" type="success" size="small">上架</el-tag>
                <el-tag v-else type="info" size="small">下架</el-tag>
                <span class="create-time">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(novelData.createTime) }}
                </span>
              </div>

              <div class="stats">
                <div class="stat-item">
                  <div class="stat-value">{{ novelData.viewCount }}</div>
                  <div class="stat-label">阅读</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ novelData.chapterCount }}</div>
                  <div class="stat-label">章节</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ formatWordCount(novelData.wordCount) }}</div>
                  <div class="stat-label">字数</div>
                </div>
              </div>

              <div class="novel-description">
                <h3>作品简介</h3>
                <p>{{ novelData.description }}</p>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 章节列表 -->
        <div class="chapter-section">
          <div class="section-header">
            <h2>章节列表</h2>
            <div class="chapter-count">
              共 {{ chapterTotal }} 章
            </div>
          </div>
          <div v-loading="chapterLoading" class="chapter-list">
            <div v-if="chapters.length === 0" class="empty-chapters">
              <el-empty description="暂无章节" />
            </div>
            <div v-else class="chapter-items">
              <div
                v-for="chapter in chapters"
                :key="chapter.id"
                class="chapter-item"
                @click="readChapter(chapter)"
              >
                <div class="chapter-title">
                  <span class="chapter-number">第{{ chapter.chapterNumber }}章</span>
                  <span class="chapter-name">{{ chapter.title }}</span>
                </div>
                <div class="chapter-meta">
                  <span class="word-count">
                    <el-icon><EditPen /></el-icon>
                    {{ chapter.wordCount }}字
                  </span>
                  <span class="create-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(chapter.createTime) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="chapters.length > 0" class="chapter-pagination">
            <el-pagination
              v-model:current-page="chapterParams.page"
              v-model:page-size="chapterParams.size"
              :total="chapterTotal"
              :page-sizes="[20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleChapterSizeChange"
              @current-change="handleChapterPageChange"
            />
          </div>
        </div>
      </div>

      <!-- 评论区域 -->
      <div v-if="novel" class="comment-section">
        <div class="section-header">
          <h2>评论</h2>
        </div>
        <div class="comment-container">
          <CommentForm
            :novel-id="novelData.id"
            @comment-added="handleCommentAdded"
          />
          <CommentList
            ref="commentListRef"
            :novel-id="novelData.id"
          />
        </div>
      </div>

      <!-- 小说不存在 -->
      <div v-else class="not-found">
        <el-empty description="小说不存在" />
      </div>
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <p>© 2026 小说网站 - 仅供学习使用</p>
    </el-footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import { getNovelById } from '@/api/novel'
import { getChapters } from '@/api/chapter'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import type { NovelVO, ChapterVO, ChapterQueryParams } from '@/types'
import { ElMessage } from 'element-plus'
import CommentForm from '@/views/CommentForm.vue'
import CommentList from '@/views/CommentList.vue'
import {
  User,
  Clock,
  EditPen,
  Reading,
  Star
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 小说数据
const novel = ref<NovelVO | null>(null)
const loading = ref(false)

// 非空小说数据（用于模板，避免类型检查错误）
const novelData = computed(() => novel.value!)

// 获取完整封面 URL
const getFullCoverUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 拼接完整 URL: http://localhost:8080/api + /uploads/covers/xxx.jpg
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseUrl}${url}`
}

// 收藏状态
const isFavorited = ref(false)
const favoriteLoading = ref(false)

// 临时用户ID
const tempUserId = ref<string>()

// 章节数据
const chapters = ref<ChapterVO[]>([])
const chapterTotal = ref(0)
const chapterLoading = ref(false)

// 评论列表引用
const commentListRef = ref<InstanceType<typeof CommentList>>()

// 章节查询参数
const chapterParams = reactive<ChapterQueryParams>({
  novelId: 0,
  page: 1,
  size: 20
})

// 初始化临时用户ID
const initTempUserId = () => {
  const stored = localStorage.getItem('tempUserId')
  if (stored) {
    tempUserId.value = stored
  } else {
    tempUserId.value = 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    localStorage.setItem('tempUserId', tempUserId.value)
  }
}

// 检查收藏状态
const checkFavoriteStatus = async (novelId: number) => {
  if (!novelId) return

  try {
    isFavorited.value = await checkFavorite(novelId)
  } catch (err) {
    console.error('检查收藏状态失败:', err)
  }
}

// 收藏/取消收藏
const handleFavorite = async () => {
  if (!novel.value || favoriteLoading.value) return

  favoriteLoading.value = true
  try {
    const result = await toggleFavorite(novel.value.id)
    isFavorited.value = result
    ElMessage.success(result ? '收藏成功' : '已取消收藏')
  } catch (err: any) {
    console.error('收藏操作失败:', err)
    ElMessage.error(err.message || '操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

// 开始阅读
const startReading = () => {
  if (!novel.value) return

  // 如果有章节，跳转到第一章
  if (chapters.value.length > 0) {
    router.push(`/chapters/${chapters.value[0].id}`)
  } else {
    ElMessage.warning('暂无章节内容')
  }
}

// 加载小说详情
const loadNovel = async (id: number) => {
  loading.value = true
  try {
    novel.value = await getNovelById(id)
    // 加载小说后检查收藏状态
    if (novel.value) {
      await checkFavoriteStatus(novel.value.id)
      // 动态设置页面标题和 meta 描述
      useHead({
        title: `《${novel.value.title}》- 在线阅读`,
        meta: [
          {
            name: 'description',
            content: novel.value.description || `《${novel.value.title}》是一部由${novel.value.author}创作的小说，欢迎在线阅读。`
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载小说详情失败:', error)
    novel.value = null
  } finally {
    loading.value = false
  }
}

// 加载章节列表
const loadChapters = async () => {
  if (!chapterParams.novelId) return

  chapterLoading.value = true
  try {
    const result = await getChapters(chapterParams)
    chapters.value = result.records
    chapterTotal.value = result.total
  } catch (error) {
    console.error('加载章节列表失败:', error)
    chapters.value = []
    chapterTotal.value = 0
  } finally {
    chapterLoading.value = false
  }
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

// 格式化字数
const formatWordCount = (count: number) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count.toString()
}

// 分页处理
const handleChapterSizeChange = (size: number) => {
  chapterParams.size = size
  chapterParams.page = 1
  loadChapters()
}

const handleChapterPageChange = (page: number) => {
  chapterParams.page = page
  loadChapters()
}

// 阅读章节
const readChapter = (_chapter: ChapterVO) => {
  router.push(`/chapters/${_chapter.id}`)
}

// 监听路由参数变化
watch(
  () => route.params.id,
  (newId) => {
    const id = Number(newId)
    if (id) {
      novel.value = null
      chapters.value = []
      chapterParams.novelId = id
      chapterParams.page = 1
      loadNovel(id)
      loadChapters()
    }
  },
  { immediate: true }
)

// 处理评论添加后刷新评论列表
const handleCommentAdded = () => {
  if (commentListRef.value) {
    commentListRef.value.refresh()
  }
}

// 初始化加载
onMounted(() => {
  // 初始化临时用户ID
  initTempUserId()

  const id = Number(route.params.id)
  if (id) {
    chapterParams.novelId = id
    loadNovel(id)
    loadChapters()
  }
})
</script>

<style scoped>
.novel-detail-page {
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

.loading {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}

.novel-detail {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.cover-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cover-image {
  width: 100%;
  max-width: 300px;
  height: 450px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  margin-bottom: 20px;
}

.cover-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  max-width: 300px;
}

.read-btn, .collect-btn {
  width: 100%;
}

.info-section {
  padding-left: 20px;
}

.novel-title {
  font-size: 32px;
  margin: 0 0 20px 0;
  color: #333;
  font-weight: 600;
  line-height: 1.3;
}

.novel-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  font-size: 14px;
  color: #666;
}

.novel-meta .el-icon {
  margin-right: 5px;
}

.stats {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.novel-description h3 {
  font-size: 18px;
  margin: 0 0 15px 0;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.novel-description p {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
}

.chapter-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #e4e7ed;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.chapter-count {
  font-size: 14px;
  color: #666;
  background: #f5f7fa;
  padding: 5px 15px;
  border-radius: 20px;
}

.chapter-list {
  min-height: 200px;
}

.empty-chapters {
  padding: 40px 0;
}

.chapter-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chapter-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background: #f9f9f9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 4px solid transparent;
}

.chapter-item:hover {
  background: #f0f7ff;
  border-left-color: #409eff;
  transform: translateX(5px);
}

.chapter-title {
  flex: 1;
}

.chapter-number {
  font-size: 14px;
  color: #409eff;
  font-weight: 600;
  margin-right: 10px;
}

.chapter-name {
  font-size: 16px;
  color: #333;
}

.chapter-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}

.chapter-meta .el-icon {
  margin-right: 3px;
}

.chapter-pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
}

.not-found {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  text-align: center;
}

.footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 14px;
}

/* 评论区域 */
.comment-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #e4e7ed;
}

.comment-section .section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.comment-section .section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.comment-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

  .novel-detail {
    padding: 20px;
  }

  .novel-title {
    font-size: 24px;
  }

  .stats {
    flex-direction: column;
    gap: 20px;
    padding: 15px;
  }

  .chapter-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .chapter-meta {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .novel-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>