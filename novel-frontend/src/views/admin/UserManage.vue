<template>
  <div class="user-manage">
    <div class="header">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索用户名/邮箱"
        style="width: 300px; margin-right: 16px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="userList" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
            {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            :type="row.status === 1 ? 'danger' : 'success'"
            size="small"
            :disabled="row.role === 'ADMIN'"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminUsers, updateUserStatus } from '@/api/adminUser'
import type { UserVO, AdminUserQueryParams } from '@/types'

// 数据
const loading = ref(false)
const userList = ref<UserVO[]>([])
const total = ref(0)

// 查询参数
const queryParams = reactive<AdminUserQueryParams>({
  keyword: '',
  page: 1,
  size: 10
})

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getAdminUsers(queryParams)
    userList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadUsers()
}

// 重置
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.page = 1
  loadUsers()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  queryParams.page = 1
  loadUsers()
}

// 页码变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  loadUsers()
}

// 切换用户状态
const handleToggleStatus = (row: UserVO) => {
  if (row.role === 'ADMIN') {
    ElMessage.warning('不能修改管理员状态')
    return
  }

  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  const confirmText = `确定${actionText}用户 "${row.username}" 吗？`

  ElMessageBox.confirm(confirmText, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateUserStatus(row.id, newStatus)
      ElMessage.success(`${actionText}成功`)
      loadUsers()
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
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