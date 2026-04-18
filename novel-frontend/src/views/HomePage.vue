<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h1>小说网站</h1>
        </div>
        <div class="nav">
          <el-button type="text" @click="$router.push('/')">首页</el-button>
          <el-button type="text" @click="$router.push('/novels')">全部小说</el-button>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main">
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
                  :src="novel.coverImage || 'https://via.placeholder.com/200x300?text=封面'"
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
              :src="novel.coverImage || 'https://via.placeholder.com/160x240?text=封面'"
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
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <p>© 2026 小说网站 - 仅供学习使用</p>
    </el-footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories } from '@/api/category'
import { getNovels, getHotNovels } from '@/api/novel'
import type { CategoryVO, NovelVO, NovelQueryParams } from '@/types'
import { View, Document } from '@element-plus/icons-vue'

const router = useRouter()

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
    hotNovels.value = await getHotNovels()
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
.home-page {
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