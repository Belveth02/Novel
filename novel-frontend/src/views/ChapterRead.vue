<template>
  <div class="chapter-read" :class="{ 'dark-mode': darkMode }">
    <!-- 顶部工具栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="left-tools">
          <el-button type="text" @click="goBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <div class="novel-info">
            <h2 class="novel-title">{{ novelTitle }}</h2>
            <span class="chapter-title"> - {{ chapter?.title }}</span>
          </div>
        </div>
        <div class="right-tools">
          <!-- 字体大小调整 -->
          <el-dropdown @command="handleFontSizeCommand">
            <el-button type="text">
              <el-icon><Operation /></el-icon> 字体: {{ fontSize }}px
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="size in [14, 16, 18, 20, 22]" :key="size" :command="size">
                  字体 {{ size }}px
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 夜间模式切换 -->
          <el-button type="text" @click="toggleDarkMode">
            <el-icon v-if="darkMode"><Sunny /></el-icon>
            <el-icon v-else><Moon /></el-icon>
            {{ darkMode ? '日间模式' : '夜间模式' }}
          </el-button>

          <!-- 章节导航 -->
          <el-dropdown @command="handleChapterCommand">
            <el-button type="text">
              <el-icon><Menu /></el-icon> 章节
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="chapter?.prevChapterId" command="prev">
                  上一章
                </el-dropdown-item>
                <el-dropdown-item v-if="chapter?.nextChapterId" command="next">
                  下一章
                </el-dropdown-item>
                <el-dropdown-item divided command="catalog">
                  目录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 阅读区域 -->
    <el-main class="main">
      <div class="reading-container">
        <!-- 章节内容 -->
        <div class="chapter-content" :style="{ fontSize: fontSize + 'px' }">
          <div v-if="loading" class="loading">
            <el-skeleton :rows="10" animated style="width: 100%;">
              <template #template>
                <el-skeleton-item variant="h1" style="width: 50%; height: 40px; margin-bottom: 30px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 90%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 95%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 80%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 20px;" />
                <el-skeleton-item variant="text" style="width: 60%; margin-bottom: 40px;" />
                <div style="display: flex; justify-content: space-between; margin-top: 40px; padding-top: 20px; border-top: 1px solid #e4e7ed;">
                  <el-skeleton-item variant="button" style="width: 100px; height: 40px;" />
                  <el-skeleton-item variant="button" style="width: 100px; height: 40px;" />
                  <el-skeleton-item variant="button" style="width: 100px; height: 40px;" />
                </div>
              </template>
            </el-skeleton>
          </div>
          <div v-else-if="error" class="error">
            <el-icon><Warning /></el-icon>
            <p>{{ error }}</p>
            <el-button type="primary" @click="loadChapter">重试</el-button>
          </div>
          <div v-else-if="chapter">
            <!-- 章节标题 -->
            <h1 class="chapter-heading">{{ chapter.title }}</h1>

            <!-- 章节内容 -->
            <div class="content-text" ref="contentRef">
              <div v-for="(paragraph, index) in formattedContent" :key="index" class="paragraph">
                {{ paragraph }}
              </div>
            </div>

            <!-- 章节导航 -->
            <div class="chapter-navigation">
              <el-button
                v-if="chapter.prevChapterId"
                type="primary"
                @click="goToChapter(chapter.prevChapterId!)"
                :loading="navLoading"
              >
                <el-icon><ArrowLeft /></el-icon> 上一章
              </el-button>
              <el-button type="text" @click="goToCatalog">返回目录</el-button>
              <el-button
                v-if="chapter.nextChapterId"
                type="primary"
                @click="goToChapter(chapter.nextChapterId!)"
                :loading="navLoading"
              >
                下一章 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
          <div v-else class="empty">
            <el-icon><Document /></el-icon>
            <p>章节不存在</p>
          </div>
        </div>
      </div>
    </el-main>

    <!-- 底部进度条 -->
    <el-footer class="footer">
      <div class="progress-container">
        <span>阅读进度</span>
        <el-progress
          :percentage="readProgress"
          :stroke-width="8"
          :show-text="false"
          class="progress-bar"
        />
        <span>{{ readProgress }}%</span>
      </div>
    </el-footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getChapterDetail } from '@/api/chapter'
import { saveReadingHistory } from '@/api/readingHistory'
import type { ChapterDetailVO } from '@/types'
import {
  ArrowLeft, ArrowRight, Operation, Moon, Sunny,
  Menu, Warning, Document
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 路由参数
const chapterId = ref<number>(parseInt(route.params.id as string))
const novelId = ref<number>()

// 数据状态
const chapter = ref<ChapterDetailVO>()
const loading = ref(false)
const error = ref('')
const navLoading = ref(false)

// 阅读设置
const fontSize = ref(16)
const darkMode = ref(false)
const readProgress = ref(0)

// 临时用户ID
const tempUserId = ref<string>()

// DOM引用
const contentRef = ref<HTMLElement>()

// 计算属性
const novelTitle = computed(() => chapter.value?.novelTitle || '')
const formattedContent = computed(() => {
  if (!chapter.value?.content) return []
  return chapter.value.content.split('\n').filter(p => p.trim() !== '')
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

// 加载章节详情
const loadChapter = async () => {
  if (!chapterId.value) {
    error.value = '章节ID无效'
    return
  }

  loading.value = true
  error.value = ''

  try {
    chapter.value = await getChapterDetail(chapterId.value)
    novelId.value = chapter.value?.novelId

    // 加载完成后更新阅读进度
    updateReadProgress()

    // 保存阅读记录
    if (novelId.value && chapter.value) {
      await saveReadingRecord()
    }
  } catch (err: any) {
    console.error('加载章节失败:', err)
    error.value = err.message || '加载章节失败，请重试'
  } finally {
    loading.value = false
  }
}

// 保存阅读记录
const saveReadingRecord = async () => {
  if (!novelId.value || !chapter.value || !tempUserId.value) return

  try {
    // 计算阅读进度（简单实现：当前章节序号 / 总章节数 * 100）
    // 这里假设总章节数未知，暂时使用100%表示已阅读
    const progress = 100

    // 使用默认测试用户ID 2（对应数据库中的测试用户）
    await saveReadingHistory({
      userId: 2, // 默认测试用户ID
      novelId: novelId.value,
      chapterId: chapterId.value,
      progress
    })
  } catch (err) {
    console.error('保存阅读记录失败:', err)
    // 不阻塞主要功能，仅记录错误
  }
}

// 更新阅读进度
const updateReadProgress = () => {
  if (!contentRef.value) return

  // 简单实现：滚动位置计算
  const updateProgress = () => {
    if (!contentRef.value) return

    const scrollTop = contentRef.value.scrollTop
    const scrollHeight = contentRef.value.scrollHeight
    const clientHeight = contentRef.value.clientHeight

    if (scrollHeight > clientHeight) {
      const progress = Math.round((scrollTop / (scrollHeight - clientHeight)) * 100)
      readProgress.value = Math.min(100, Math.max(0, progress))
    } else {
      readProgress.value = 100
    }
  }

  contentRef.value.addEventListener('scroll', updateProgress)
  updateProgress()
}

// 导航功能
const goToChapter = async (targetChapterId: number) => {
  navLoading.value = true
  try {
    await router.push(`/chapters/${targetChapterId}`)
    chapterId.value = targetChapterId
    await loadChapter()
  } catch (err) {
    console.error('跳转章节失败:', err)
  } finally {
    navLoading.value = false
  }
}

const goToCatalog = () => {
  if (novelId.value) {
    router.push(`/novels/${novelId.value}`)
  } else {
    router.back()
  }
}

const goBack = () => {
  router.back()
}

// 工具栏功能
const handleFontSizeCommand = (size: number) => {
  fontSize.value = size
  localStorage.setItem('readingFontSize', size.toString())
}

const handleChapterCommand = (command: string) => {
  switch (command) {
    case 'prev':
      if (chapter.value?.prevChapterId) {
        goToChapter(chapter.value.prevChapterId)
      }
      break
    case 'next':
      if (chapter.value?.nextChapterId) {
        goToChapter(chapter.value.nextChapterId)
      }
      break
    case 'catalog':
      goToCatalog()
      break
  }
}

const toggleDarkMode = () => {
  darkMode.value = !darkMode.value
  localStorage.setItem('readingDarkMode', darkMode.value.toString())
}

// 监听路由变化
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      chapterId.value = parseInt(newId as string)
      loadChapter()
    }
  }
)

// 初始化
onMounted(() => {
  // 恢复用户设置
  const savedFontSize = localStorage.getItem('readingFontSize')
  if (savedFontSize) {
    fontSize.value = parseInt(savedFontSize)
  }

  const savedDarkMode = localStorage.getItem('readingDarkMode')
  if (savedDarkMode) {
    darkMode.value = savedDarkMode === 'true'
  }

  initTempUserId()
  loadChapter()
})

onUnmounted(() => {
  // 清理事件监听器
  if (contentRef.value) {
    // 实际应该保存事件监听器的引用以便移除，这里简化处理
  }
})
</script>

<style scoped>
.chapter-read {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  transition: background-color 0.3s, color 0.3s;
}

.chapter-read.dark-mode {
  background-color: #1a1a1a;
  color: #e0e0e0;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  transition: background-color 0.3s, border-color 0.3s;
}

.dark-mode .header {
  background: #2d2d2d;
  border-bottom-color: #404040;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.left-tools {
  display: flex;
  align-items: center;
  gap: 20px;
}

.novel-info {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.novel-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dark-mode .novel-title {
  color: #e0e0e0;
}

.chapter-title {
  font-size: 14px;
  color: #666;
}

.dark-mode .chapter-title {
  color: #a0a0a0;
}

.right-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.main {
  flex: 1;
  padding: 20px;
  display: flex;
  justify-content: center;
}

.reading-container {
  width: 100%;
  max-width: 800px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: background-color 0.3s;
}

.dark-mode .reading-container {
  background: #2d2d2d;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.chapter-content {
  min-height: 400px;
}

.loading, .error, .empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  text-align: center;
}

.loading-icon {
  font-size: 40px;
  color: #409eff;
  margin-bottom: 20px;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error .el-icon {
  font-size: 40px;
  color: #f56c6c;
  margin-bottom: 20px;
}

.empty .el-icon {
  font-size: 40px;
  color: #909399;
  margin-bottom: 20px;
}

.chapter-heading {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
  border-bottom: 2px solid #409eff;
  padding-bottom: 15px;
}

.dark-mode .chapter-heading {
  color: #e0e0e0;
  border-bottom-color: #409eff;
}

.content-text {
  line-height: 1.8;
  margin-bottom: 40px;
}

.paragraph {
  margin-bottom: 1.5em;
  text-indent: 2em;
  text-align: justify;
}

.chapter-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.dark-mode .chapter-navigation {
  border-top-color: #404040;
}

.footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 10px 20px;
  transition: background-color 0.3s, border-color 0.3s;
}

.dark-mode .footer {
  background: #2d2d2d;
  border-top-color: #404040;
}

.progress-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.progress-bar {
  flex: 1;
  max-width: 600px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    padding: 10px 0;
    gap: 10px;
  }

  .left-tools {
    width: 100%;
    justify-content: space-between;
  }

  .right-tools {
    width: 100%;
    justify-content: center;
  }

  .novel-title {
    max-width: 200px;
  }

  .reading-container {
    padding: 20px;
  }

  .chapter-heading {
    font-size: 24px;
  }

  .chapter-navigation {
    flex-direction: column;
    gap: 15px;
  }
}
</style>