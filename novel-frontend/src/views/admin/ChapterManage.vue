<template>
  <div class="chapter-manage">
    <div class="header">
      <el-select
        v-model="queryParams.novelId"
        placeholder="请选择小说"
        clearable
        filterable
        style="width: 300px; margin-right: 16px"
        @change="handleNovelChange"
      >
        <el-option
          v-for="novel in novelList"
          :key="novel.id"
          :label="novel.title"
          :value="novel.id"
        />
      </el-select>
      <el-button type="primary" :disabled="!queryParams.novelId" @click="handleCreate">新增章节</el-button>
    </div>

    <el-table :data="chapterList" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="chapterNumber" label="序号" width="80" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="wordCount" label="字数" width="100">
        <template #default="{ row }">
          {{ row.wordCount }}字
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
        label-position="left"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入章节标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="20"
            placeholder="请输入章节内容"
            resize="none"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getAdminChapters, getAdminChapterById, createAdminChapter, updateAdminChapter, deleteAdminChapter } from '@/api/adminChapter'
import { getAdminNovels } from '@/api/adminNovel'
import type { ChapterVO, NovelVO, AdminChapterQueryParams, AdminChapterCreateParams, AdminChapterUpdateParams } from '@/types'

// 数据
const loading = ref(false)
const chapterList = ref<ChapterVO[]>([])
const novelList = ref<NovelVO[]>([])
const total = ref(0)

// 查询参数
const queryParams = reactive<AdminChapterQueryParams>({
  novelId: 0,
  page: 1,
  size: 10
})

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: 0,
  title: '',
  content: ''
})

// 表单验证规则
const formRules: FormRules = {
  title: [{ required: true, message: '请输入章节标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入章节内容', trigger: 'blur' }]
}

// 加载小说列表（用于下拉框）
const loadNovels = async () => {
  try {
    const res = await getAdminNovels({ page: 1, size: 1000 }) // 获取足够多的小说
    novelList.value = res.records
  } catch (error) {
    console.error('加载小说列表失败:', error)
    ElMessage.error('加载小说列表失败')
  }
}

// 加载章节列表
const loadChapters = async () => {
  if (!queryParams.novelId) {
    chapterList.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    const res = await getAdminChapters(queryParams)
    // 数据转换：确保所有字段都有值
    chapterList.value = res.records.map(item => ({
      ...item,
      wordCount: item.wordCount ?? 0,
      createTime: item.createTime ?? ''
    }))
    total.value = res.total
  } catch (error) {
    console.error('加载章节列表失败:', error)
    ElMessage.error('加载章节列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

// 小说切换
const handleNovelChange = () => {
  queryParams.page = 1
  loadChapters()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  queryParams.page = 1
  loadChapters()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  loadChapters()
}

// 新增章节
const handleCreate = () => {
  if (!queryParams.novelId) {
    ElMessage.warning('请先选择小说')
    return
  }
  dialogTitle.value = '新增章节'
  Object.assign(formData, {
    id: 0,
    title: '',
    content: ''
  })
  dialogVisible.value = true
}

// 编辑章节
const handleEdit = (row: ChapterVO) => {
  dialogTitle.value = '编辑章节'
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    content: ''
  })
  loadChapterContent(row.id)
}

// 加载章节内容
const loadChapterContent = async (chapterId: number) => {
  try {
    const chapterDetail = await getAdminChapterById(chapterId)
    formData.content = chapterDetail.content
    dialogVisible.value = true
  } catch (error) {
    console.error('加载章节内容失败:', error)
    ElMessage.error('加载章节内容失败')
  }
}

// 删除章节
const handleDelete = (row: ChapterVO) => {
  ElMessageBox.confirm(`确定删除章节《${row.title}》吗？删除后无法恢复。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminChapter(row.id)
      ElMessage.success('删除成功')
      loadChapters()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 对话框关闭
const handleDialogClose = (done: () => void) => {
  ElMessageBox.confirm('确定要关闭吗？已填写的内容将不会被保存。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    done()
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (formData.id) {
      // 更新
      const updateParams: AdminChapterUpdateParams = {
        title: formData.title,
        content: formData.content
      }
      await updateAdminChapter(formData.id, updateParams)
      ElMessage.success('更新成功')
    } else {
      // 新增
      const createParams: AdminChapterCreateParams = {
        novelId: queryParams.novelId,
        title: formData.title,
        content: formData.content
      }
      await createAdminChapter(createParams)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadChapters()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(() => {
  loadNovels()
})
</script>

<style scoped>
.chapter-manage {
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>