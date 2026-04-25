<template>
  <div class="comment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
        </div>
      </template>

      <!-- 小说选择 -->
      <el-form inline class="search-form">
        <el-form-item label="选择小说">
          <el-select
            v-model="selectedNovelId"
            placeholder="请选择要查看评论的小说"
            style="width: 300px"
            @change="handleNovelChange"
          >
            <el-option
              v-for="novel in novels"
              :key="novel.id"
              :label="novel.title"
              :value="novel.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <el-empty v-if="!selectedNovelId" description="请先选择一本小说" />

      <template v-else>
        <el-empty v-if="!loading && commentList.length === 0" description="暂无评论" />

        <el-table
          v-else
          v-loading="loading"
          :data="commentList"
          style="width: 100%"
          border
        >
          <el-table-column label="评论用户" width="200">
            <template #default="{ row }">
              <div class="user-info">
                <el-avatar :size="40" :src="getAvatarUrl(row.avatar) || undefined" />
                <div class="user-detail">
                  <div class="nickname">{{ row.nickname || row.username }}</div>
                  <div class="username" v-if="row.nickname">@{{ row.username }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="评论内容" min-width="300">
            <template #default="{ row }">
              <div class="comment-content">{{ row.content }}</div>
            </template>
          </el-table-column>

          <el-table-column prop="createTime" label="评论时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, del } from '@/api'

const loading = ref(false)
const novels = ref([])
const selectedNovelId = ref<number | null>(null)
const commentList = ref([])
const total = ref(0)

const queryParams = ref({
  page: 1,
  size: 10
})

const fetchNovels = async () => {
  try {
    const result = await get('/author/novels', {
      params: { page: 1, size: 100 }
    })
    novels.value = result.records || []
  } catch (error: any) {
    ElMessage.error(error.message || '获取小说列表失败')
  }
}

const fetchComments = async () => {
  if (!selectedNovelId.value) return

  loading.value = true
  try {
    const result = await get('/author/comments', {
      params: {
        novelId: selectedNovelId.value,
        ...queryParams.value
      }
    })
    commentList.value = result.records || []
    total.value = result.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '获取评论列表失败')
  } finally {
    loading.value = false
  }
}

const handleNovelChange = () => {
  queryParams.value.page = 1
  fetchComments()
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getAvatarUrl = (avatar: string | null | undefined) => {
  if (!avatar) return ''
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }

  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  if (avatar.startsWith('/api/')) {
    return `${window.location.origin}${avatar}`
  }
  if (avatar.startsWith('/')) {
    return `${baseUrl}${avatar}`
  }
  return `${baseUrl}/${avatar}`
}

const handleSizeChange = (size: number) => {
  queryParams.value.size = size
  fetchComments()
}

const handlePageChange = (page: number) => {
  queryParams.value.page = page
  fetchComments()
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除这条评论吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await del(`/author/comments/${row.id}?novelId=${selectedNovelId.value}`)
      ElMessage.success('删除成功')
      fetchComments()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

onMounted(() => {
  fetchNovels()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-weight: bold;
}

.username {
  font-size: 12px;
  color: #999;
}

.comment-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
