<template>
  <div class="novel-manage">
    <div class="header">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索小说标题/作者"
        style="width: 300px; margin-right: 16px"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="queryParams.categoryId"
        placeholder="全部分类"
        clearable
        style="width: 150px; margin-right: 16px"
      >
        <el-option
          v-for="category in categoryList"
          :key="category.id"
          :label="category.name"
          :value="category.id"
        />
      </el-select>
      <el-select
        v-model="queryParams.status"
        placeholder="全部状态"
        clearable
        style="width: 120px; margin-right: 16px"
      >
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button type="primary" style="margin-left: auto" @click="handleCreate">新增小说</el-button>
    </div>

    <el-table :data="novelList" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.coverImage"
            :src="getFullCoverUrl(row.coverImage)"
            fit="cover"
            style="width: 60px; height: 80px; border-radius: 4px"
          />
          <div v-else style="width: 60px; height: 80px; background: #f5f5f5; border-radius: 4px; display: flex; align-items: center; justify-content: center">
            <span style="color: #999; font-size: 12px">无封面</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column label="分类" width="120">
        <template #default="{ row }">
          {{ getCategoryName(row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="chapterCount" label="章节数" width="80" />
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
      <el-table-column label="热门推荐" width="100" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.isHot"
            :active-value="1"
            :inactive-value="0"
            @change="(val: number) => handleHotChange(row, val)"
          />
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
      width="600px"
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
          <el-input v-model="formData.title" placeholder="请输入小说标题" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <div class="cover-upload">
            <el-image
              v-if="formData.coverImage"
              :src="getFullCoverUrl(formData.coverImage)"
              fit="cover"
              style="width: 120px; height: 160px; border-radius: 4px; margin-right: 16px"
            />
            <div v-else style="width: 120px; height: 160px; background: #f5f5f5; border-radius: 4px; display: flex; align-items: center; justify-content: center; margin-right: 16px">
              <span style="color: #999; font-size: 12px">暂无封面</span>
            </div>
            <el-upload
              class="cover-uploader"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleCoverChange"
              accept="image/*"
            >
              <el-button type="primary">选择图片</el-button>
              <template #tip>
                <div class="el-upload__tip">支持 jpg/png 格式，大小不超过 2MB</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="简介">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入小说简介"
          />
        </el-form-item>
        <el-form-item label="热门排序" v-if="formData.isHot === 1">
          <el-input-number
            v-model="formData.hotSort"
            :min="0"
            :max="9999"
            placeholder="数值越大排序越靠前"
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
import { getAdminNovels, createAdminNovel, updateAdminNovel, deleteAdminNovel, uploadNovelCover } from '@/api/adminNovel'
import { getCategories } from '@/api/category'
import type { NovelVO, AdminNovelQueryParams, AdminNovelCreateParams, AdminNovelUpdateParams, CategoryVO } from '@/types'

// 数据
const loading = ref(false)
const novelList = ref<NovelVO[]>([])
const total = ref(0)
const categoryList = ref<CategoryVO[]>([])

// 查询参数
const queryParams = reactive<AdminNovelQueryParams>({
  keyword: '',
  page: 1,
  size: 10,
  categoryId: undefined,
  status: undefined
})

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: 0,
  title: '',
  author: '',
  coverImage: '',
  description: '',
  categoryId: undefined as number | undefined,
  status: 1,
  isHot: 0,
  hotSort: 0
})

// 表单验证规则
const formRules: FormRules = {
  title: [{ required: true, message: '请输入小说标题', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await getCategories()
    categoryList.value = res
  } catch (error) {
    console.error('加载分类列表失败:', error)
    ElMessage.error('加载分类列表失败')
  }
}

// 加载小说列表
const loadNovels = async () => {
  loading.value = true
  try {
    const res = await getAdminNovels(queryParams)
    // 数据转换：确保所有字段都有值
    novelList.value = res.records.map(item => ({
      ...item,
      categoryId: item.categoryId ?? 0,
      status: item.status ?? 0,
      wordCount: item.wordCount ?? 0,
      createTime: item.createTime ?? '',
      coverImage: item.coverImage ?? '',
      description: item.description ?? '',
      chapterCount: item.chapterCount ?? 0,
      viewCount: item.viewCount ?? 0
    }))
    total.value = res.total
  } catch (error) {
    console.error('加载小说列表失败:', error)
    ElMessage.error('加载小说列表失败')
  } finally {
    loading.value = false
  }
}

// 根据分类ID获取分类名称
const getCategoryName = (categoryId: number) => {
  const category = categoryList.value.find(item => item.id === categoryId)
  return category ? category.name : '-'
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadNovels()
}

// 重置
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.page = 1
  loadNovels()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  queryParams.page = 1
  loadNovels()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  loadNovels()
}

// 新增小说
const handleCreate = () => {
  dialogTitle.value = '新增小说'
  Object.assign(formData, {
    id: 0,
    title: '',
    author: '',
    coverImage: '',
    description: '',
    categoryId: undefined,
    status: 1,
    isHot: 0,
    hotSort: 0
  })
  dialogVisible.value = true
}

// 编辑小说
const handleEdit = (row: NovelVO) => {
  dialogTitle.value = '编辑小说'
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    author: row.author,
    coverImage: row.coverImage || '',
    description: row.description || '',
    categoryId: row.categoryId,
    status: row.status,
    isHot: row.isHot ?? 0,
    hotSort: row.hotSort ?? 0
  })
  dialogVisible.value = true
}

// 热门推荐开关变化
const handleHotChange = async (row: NovelVO, val: number) => {
  try {
    await updateAdminNovel(row.id, {
      isHot: val,
      hotSort: val === 1 ? (row.hotSort || 0) : 0
    })
    ElMessage.success(val === 1 ? '已设为热门推荐' : '已取消热门推荐')
  } catch (error) {
    console.error('设置热门推荐失败:', error)
    ElMessage.error('设置失败')
    // 恢复原状
    row.isHot = val === 1 ? 0 : 1
  }
}

// 删除小说
const handleDelete = (row: NovelVO) => {
  ElMessageBox.confirm(`确定删除小说《${row.title}》吗？删除后无法恢复，且会删除所有章节。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminNovel(row.id)
      ElMessage.success('删除成功')
      loadNovels()
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

// 处理封面变更
const handleCoverChange = async (file: any) => {
  if (!file.raw) return

  // 验证文件类型
  const isImage = file.raw.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return
  }

  // 验证文件大小（限制为2MB）
  const isLt2M = file.raw.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  try {
    const coverUrl = await uploadNovelCover(file.raw)
    formData.coverImage = coverUrl
    ElMessage.success('封面上传成功')
  } catch (error) {
    console.error('封面上传失败:', error)
    ElMessage.error('封面上传失败')
  }
}

// 获取完整封面URL
const getFullCoverUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 拼接完整URL
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseUrl}${url}`
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
      const updateParams: AdminNovelUpdateParams = {
        title: formData.title,
        author: formData.author,
        coverImage: formData.coverImage || undefined,
        description: formData.description || undefined,
        categoryId: formData.categoryId,
        status: formData.status,
        isHot: formData.isHot,
        hotSort: formData.hotSort
      }
      await updateAdminNovel(formData.id, updateParams)
      ElMessage.success('更新成功')
    } else {
      // 新增
      const createParams: AdminNovelCreateParams = {
        title: formData.title,
        author: formData.author,
        coverImage: formData.coverImage || undefined,
        description: formData.description || undefined,
        categoryId: formData.categoryId!,
        status: formData.status
      }
      await createAdminNovel(createParams)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadNovels()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(() => {
  loadCategories()
  loadNovels()
})
</script>

<style scoped>
.novel-manage {
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

.cover-upload {
  display: flex;
  align-items: flex-start;
}

.cover-uploader {
  display: flex;
  flex-direction: column;
}

.cover-uploader .el-upload__tip {
  margin-top: 8px;
  color: #909399;
}
</style>