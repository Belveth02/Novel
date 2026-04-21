<template>
  <div class="category-manage">
    <div class="header">
      <el-button type="primary" @click="handleCreate">新增分类</el-button>
    </div>

    <el-table :data="categoryList" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="novelCount" label="小说数量" width="100">
        <template #default="{ row }">
          <el-tag :type="row.novelCount > 0 ? 'warning' : 'info'" size="small">
            {{ row.novelCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序序号" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="160">
        <template #default="{ row }">
          {{ formatTime(row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)" :loading="row.id === deletingId">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
        label-position="left"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
          />
        </el-form-item>
        <el-form-item label="排序序号">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :step="1"
            placeholder="请输入排序序号（可选）"
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getAdminCategories, createAdminCategory, updateAdminCategory, deleteAdminCategory } from '@/api/adminCategory'
import type { AdminCategoryVO, AdminCategoryCreateParams, AdminCategoryUpdateParams } from '@/types'

// 数据
const loading = ref(false)
const categoryList = ref<AdminCategoryVO[]>([])
const deletingId = ref(0)

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: 0,
  name: '',
  description: '',
  sortOrder: 0
})

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 加载分类列表
const loadCategories = async () => {
  loading.value = true
  try {
    const res = await getAdminCategories()
    // 数据转换：确保所有字段都有值
    categoryList.value = res.map(item => ({
      ...item,
      novelCount: item.novelCount ?? 0,
      createTime: item.createTime ?? '',
      updateTime: item.updateTime ?? '',
      description: item.description ?? '',
      sortOrder: item.sortOrder ?? 0
    }))
  } catch (error) {
    console.error('加载分类列表失败:', error)
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

// 新增分类
const handleCreate = () => {
  dialogTitle.value = '新增分类'
  Object.assign(formData, {
    id: 0,
    name: '',
    description: '',
    sortOrder: 0
  })
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 编辑分类
const handleEdit = (row: AdminCategoryVO) => {
  dialogTitle.value = '编辑分类'
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    description: row.description || '',
    sortOrder: row.sortOrder || 0
  })
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 删除分类
const handleDelete = (row: AdminCategoryVO) => {
  if (row.novelCount > 0) {
    ElMessage.warning('该分类下有关联小说，无法删除')
    return
  }

  ElMessageBox.confirm(`确定删除分类 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    deletingId.value = row.id
    try {
      await deleteAdminCategory(row.id)
      ElMessage.success('删除成功')
      loadCategories()
    } catch (error: any) {
      console.error('删除失败:', error)
      // 展示后端返回的错误信息
      const errorMsg = error?.response?.data?.message || error?.message || '删除失败'
      ElMessage.error(errorMsg)
    } finally {
      deletingId.value = 0
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
      const updateParams: AdminCategoryUpdateParams = {
        name: formData.name,
        description: formData.description || undefined,
        sortOrder: formData.sortOrder || undefined
      }
      await updateAdminCategory(formData.id, updateParams)
      ElMessage.success('更新成功')
    } else {
      // 新增
      const createParams: AdminCategoryCreateParams = {
        name: formData.name,
        description: formData.description || undefined,
        sortOrder: formData.sortOrder || undefined
      }
      await createAdminCategory(createParams)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadCategories()
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
})
</script>

<style scoped>
.category-manage {
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>