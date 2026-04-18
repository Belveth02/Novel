<template>
  <div class="novel-list-page">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <h1>小说网站</h1>
        </div>
        <div class="nav">
          <el-button type="text" @click="$router.push('/')">首页</el-button>
          <el-button type="text" @click="$router.push('/novels')">全部小说</el-button>
        </div>
      </div>
    </el-header>

    <el-main class="main">
      <!-- 筛选条件 -->
      <div class="filter-bar">
        <el-row :gutter="20" class="filter-row">
          <el-col :xs="24" :sm="12" :md="6">
            <el-input
              v-model="queryParams.title"
              placeholder="搜索小说标题"
              clearable
              @clear="handleFilter"
              @keyup.enter="handleFilter"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-input
              v-model="queryParams.author"
              placeholder="搜索作者"
              clearable
              @clear="handleFilter"
              @keyup.enter="handleFilter"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-select
              v-model="queryParams.categoryId"
              placeholder="选择分类"
              clearable
              @change="handleFilter"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-select
              v-model="queryParams.sortBy"
              placeholder="排序方式"
              @change="handleFilter"
            >
              <el-option label="阅读量" value="viewCount" />
              <el-option label="最新上架" value="createTime" />
              <el-option label="字数" value="wordCount" />
            </el-select>
          </el-col>
        </el-row>
        <div class="filter-actions">
          <el-button type="primary" @click="handleFilter">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetFilter">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
      </div>

      <!-- 小说列表 -->
      <div v-loading="loading" class="novel-list">
        <div v-if="novels.length === 0" class="empty">
          <el-empty description="暂无小说数据" />
        </div>
        <div v-else class="novel-grid">
          <div
            v-for="novel in novels"
            :key="novel.id"
            class="novel-card"
            @click="goToNovelDetail(novel.id)"
          >
            <el-image
              :src="novel.coverImage || 'https://via.placeholder.com/160x240?text=封面'"
              fit="cover"
              class="novel-cover"
            />
            <div class="novel-info">
              <h4 class="novel-title">{{ novel.title }}</h4>
              <p class="novel-author">{{ novel.author }}</p>
              <p class="novel-description">{{ novel.description?.slice(0, 60) }}...</p>
              <div class="novel-stats">
                <span><el-icon><View /></el-icon> {{ novel.viewCount }}</span>
                <span><el-icon><Document /></el-icon> {{ novel.chapterCount }}章</span>
                <span><el-icon><EditPen /></el-icon> {{ novel.wordCount }}字</span>
              </div>
              <div class="novel-status">
                <el-tag v-if="novel.status === 1" type="success" size="small">上架</el-tag>
                <el-tag v-else type="info" size="small">下架</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="novels.length > 0" class="pagination">
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
import { getCategories } from '@/api/category'
import { getNovels } from '@/api/novel'
import type { CategoryVO, NovelVO, NovelQueryParams } from '@/types'
import {
  Search,
  User,
  View,
  Document,
  EditPen,
  Refresh
} from '@element-plus/icons-vue'

const router = useRouter()

// 分类数据
const categories = ref<CategoryVO[]>([])

// 小说数据
const novels = ref<NovelVO[]>([])
const total = ref(0)
const loading = ref(false)

// 查询参数
const queryParams = reactive<NovelQueryParams>({
  page: 1,
  size: 12,
  title: '',
  author: '',
  categoryId: undefined,
  sortBy: 'viewCount',
  sortOrder: 'desc'
})

// 加载分类
const loadCategories = async () => {
  try {
    categories.value = await getCategories()
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载小说列表
const loadNovels = async () => {
  loading.value = true
  try {
    const result = await getNovels(queryParams)
    novels.value = result.records
    total.value = result.total
  } catch (error) {
    console.error('加载小说列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理筛选
const handleFilter = () => {
  queryParams.page = 1
  loadNovels()
}

// 重置筛选
const resetFilter = () => {
  queryParams.page = 1
  queryParams.title = ''
  queryParams.author = ''
  queryParams.categoryId = undefined
  queryParams.sortBy = 'viewCount'
  queryParams.sortOrder = 'desc'
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

// 初始化加载
onMounted(() => {
  loadCategories()
  loadNovels()
})
</script>

<style scoped>
.novel-list-page {
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

.filter-bar {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.filter-row {
  margin-bottom: 15px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.novel-list {
  min-height: 500px;
}

.empty {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
}

.novel-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
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
  margin-bottom: 10px;
}

.novel-stats .el-icon {
  margin-right: 3px;
}

.novel-status {
  display: flex;
  justify-content: flex-end;
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

  .novel-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
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

  .filter-row .el-col {
    margin-bottom: 10px;
  }
}
</style>