<template>
  <div class="chapter-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">章节管理</span>
          </div>
        </div>
      </template>

      <!-- 小说选择 -->
      <el-form inline class="search-form">
        <el-form-item label="选择小说">
          <el-select
            v-model="selectedNovelId"
            placeholder="请选择要管理章节的小说"
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
        <el-form-item v-if="selectedNovelId">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增章节
          </el-button>
        </el-form-item>
      </el-form>

      <el-empty v-if="!selectedNovelId" description="请先选择一本小说" />

      <template v-else>
        <el-empty v-if="!loading && chapterList.length === 0" description="暂无章节" />

        <el-table
          v-else
          v-loading="loading"
          :data="chapterList"
          style="width: 100%"
          border
        >
          <el-table-column type="index" label="序号" width="80" align="center" />

          <el-table-column prop="chapterNumber" label="章节号" width="100" align="center" />

          <el-table-column prop="title" label="章节标题" min-width="300" show-overflow-tooltip />

          <el-table-column prop="wordCount" label="字数" width="120" align="center">
            <template #default="{ row }">
              {{ formatNumber(row.wordCount) }}
            </template>
          </el-table-column>

          <el-table-column prop="createTime" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            :page-sizes="[20, 50, 100]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </template>
    </el-card>

    <!-- 章节编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑章节' : '新增章节'"
      width="800px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="章节号" prop="chapterNumber">
          <el-input-number v-model="form.chapterNumber" :min="1" :precision="0" />
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入章节标题" />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="请输入章节内容"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { get, post, put, del } from '@/api'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const novels = ref([])
const selectedNovelId = ref<number | null>(null)
const chapterList = ref([])
const total = ref(0)

const queryParams = ref({
  page: 1,
  size: 100
})

const form = ref({
  id: 0,
  chapterNumber: 1,
  title: '',
  content: ''
})

const rules: FormRules = {
  chapterNumber: [
    { required: true, message: '请输入章节号', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入章节标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入章节内容', trigger: 'blur' }
  ]
}

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

const fetchChapters = async () => {
  if (!selectedNovelId.value) return

  loading.value = true
  try {
    const result = await get('/author/chapters', {
      params: {
        novelId: selectedNovelId.value,
        ...queryParams.value
      }
    })
    chapterList.value = result.records || []
    total.value = result.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '获取章节列表失败')
  } finally {
    loading.value = false
  }
}

const handleNovelChange = () => {
  queryParams.value.page = 1
  fetchChapters()
}

const formatNumber = (num: number) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
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

const handleSizeChange = (size: number) => {
  queryParams.value.size = size
  fetchChapters()
}

const handlePageChange = (page: number) => {
  queryParams.value.page = page
  fetchChapters()
}

const handleCreate = () => {
  isEdit.value = false
  form.value = {
    id: 0,
    chapterNumber: chapterList.value.length + 1,
    title: '',
    content: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  form.value = {
    id: row.id,
    chapterNumber: row.chapterNumber,
    title: row.title,
    content: ''
  }
  fetchChapterDetail(row.id)
  dialogVisible.value = true
}

const fetchChapterDetail = async (id: number) => {
  try {
    const result = await get(`/author/chapters/${id}`)
    form.value.content = result.content
  } catch (error: any) {
    ElMessage.error(error.message || '获取章节内容失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const data = {
        novelId: selectedNovelId.value,
        chapterNumber: form.value.chapterNumber,
        title: form.value.title,
        content: form.value.content
      }

      if (isEdit.value) {
        await put(`/author/chapters/${form.value.id}`, data)
        ElMessage.success('更新成功')
      } else {
        await post('/author/chapters', data)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchChapters()
    } catch (error: any) {
      ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除第${row.chapterNumber}章《${row.title}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await del(`/author/chapters/${row.id}`)
      ElMessage.success('删除成功')
      fetchChapters()
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-weight: bold;
  font-size: 16px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
