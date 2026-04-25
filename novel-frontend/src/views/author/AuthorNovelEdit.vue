<template>
  <div class="novel-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑小说' : '创建小说' }}</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 800px"
      >
        <el-form-item label="小说标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入小说标题" />
        </el-form-item>

        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者显示名称（笔名）" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="封面" prop="coverImage">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="form.coverImage" :src="getImageUrl(form.coverImage)" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <p class="upload-tip">建议尺寸：300x400，支持jpg、png格式，不超过2MB</p>
        </el-form-item>

        <el-form-item label="简介" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请输入小说简介"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item v-if="isEdit" label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { get, post, put } from '@/api'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const categories = ref([])

const isEdit = computed(() => !!route.params.id)
const novelId = computed(() => route.params.id as string)
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/author/novels/cover'

// 上传请求头（携带token）
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('author_token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

// 获取图片完整URL
const getImageUrl = (coverImage: string | null) => {
  if (!coverImage) return '/default-cover.jpg'
  if (coverImage.startsWith('http')) return coverImage
  return import.meta.env.VITE_API_BASE_URL + coverImage
}

const form = ref({
  title: '',
  author: '',
  categoryId: null as number | null,
  coverImage: '',
  description: '',
  status: 1
})

const rules: FormRules = {
  title: [
    { required: true, message: '请输入小说标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符之间', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者名称', trigger: 'blur' },
    { max: 50, message: '作者名称不能超过50个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  description: [
    { max: 2000, message: '简介不能超过2000个字符', trigger: 'blur' }
  ]
}

const fetchCategories = async () => {
  try {
    const result = await get('/categories')
    categories.value = result || []
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

const fetchNovelDetail = async () => {
  if (!isEdit.value) return
  try {
    const result = await get(`/author/novels/${novelId.value}`)
    form.value = {
      title: result.title,
      author: result.author,
      categoryId: result.categoryId,
      coverImage: result.coverImage,
      description: result.description,
      status: result.status
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取小说详情失败')
    router.push('/author/novels')
  }
}

const handleCoverSuccess = (res: any) => {
  if (res.code === 200) {
    form.value.coverImage = res.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeCoverUpload = (file: File) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJPGOrPNG) {
    ElMessage.error('封面只能是 JPG 或 PNG 格式!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('封面大小不能超过 2MB!')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      if (isEdit.value) {
        await put(`/author/novels/${novelId.value}`, form.value)
        ElMessage.success('更新成功')
      } else {
        await post('/author/novels', form.value)
        ElMessage.success('创建成功')
      }
      router.push('/author/novels')
    } catch (error: any) {
      ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
    } finally {
      loading.value = false
    }
  })
}

const goBack = () => {
  router.push('/author/novels')
}

onMounted(() => {
  fetchCategories()
  fetchNovelDetail()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
}

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 150px;
  height: 200px;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 200px;
  text-align: center;
  line-height: 200px;
}

.cover-preview {
  width: 150px;
  height: 200px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>
